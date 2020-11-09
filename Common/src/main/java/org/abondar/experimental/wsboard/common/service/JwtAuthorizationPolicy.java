package org.abondar.experimental.wsboard.common.service;

import org.apache.camel.CamelAuthorizationException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.processor.DelegateProcessor;
import org.apache.camel.spi.AuthorizationPolicy;
import org.apache.camel.spi.RouteContext;
import org.apache.cxf.security.LoginSecurityContext;
import org.apache.cxf.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthorizationPolicy implements AuthorizationPolicy {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationPolicy.class);
    private List<String> allowedRoles = new ArrayList<>();
    private boolean allowAnyRole = false;

    @Override
    public void beforeWrap(RouteContext rc, ProcessorDefinition<?> pd) {
       // throw new UnsupportedOperationException();
    }

    @Override
    public Processor wrap(RouteContext rc, Processor processor) {
        return new AuthorizeDelegateProcess(processor);
    }

    private void beforeProcess(Exchange exchange) throws CamelAuthorizationException{

        var securityContext = getSecurityContext(exchange);
        if (null == securityContext) {
            throw new CamelAuthorizationException("Cannot find the SecurityContext instance.", exchange);
        }
        if (!(LoginSecurityContext.class.isAssignableFrom(securityContext.getClass()))) {
            logger.info("the security context an instance of {}", securityContext.getClass());
            throw new CamelAuthorizationException("User was not recognized by security filters!", exchange);
        }
        var userRoles = ((LoginSecurityContext) securityContext).getUserRoles().stream()
                .map(Principal::getName).collect(Collectors.toSet());
        boolean isRolesAllowed = false;
        logger.debug("User Prinicipal - {}", getUserPrincipal(exchange));
        logger.debug("the security context contains {}", userRoles);
        if (allowAnyRole && isAuthenticated(exchange)) {
            if (userRoles.isEmpty()) {
                logger.error("User {} not autheticated!", getUserPrincipal(exchange));
                throw new CamelAuthorizationException(String.format("User %s is not authenticated!", getUserPrincipal(exchange)), exchange);
            }
            return;
        }
        logger.debug("Allowed roles {}", allowedRoles);
        for (String role : allowedRoles) {
            if (userRoles.contains(role)) {
                isRolesAllowed = true;
            }
        }
        logger.debug("User {} authorized - {}", getUserPrincipal(exchange), isRolesAllowed);
        if (!isRolesAllowed) {
            throw new CamelAuthorizationException(allowedRoles.stream().collect(Collectors.joining(",", String.format("User %s not in roles: ", getUserPrincipal(exchange)), "!")), exchange);
        }

    }

    public void setAllowAnyRole(boolean allowAnyRole) {
        this.allowAnyRole = allowAnyRole;
    }


    public JwtAuthorizationPolicy allowedRoles(List<String> allowedRoles) {
        this.allowedRoles = allowedRoles;
        this.allowAnyRole = setAllowAnyRole(this.allowedRoles);
        return this;
    }

    public void setAllowedRoles(String... allowedRoles) {
        this.allowedRoles = Arrays.asList(allowedRoles);
        this.allowAnyRole = setAllowAnyRole(this.allowedRoles);
    }


    private SecurityContext getSecurityContext(Exchange exchange) throws CamelAuthorizationException {
        try {
            return ((org.apache.cxf.message.Message) exchange.getIn()
                    .getHeader("CamelCxfMessage")).get(SecurityContext.class);
        } catch (Exception ex) {
            throw new CamelAuthorizationException("Cannot find the SecurityContext instance.", exchange, ex);
        }
    }

    private boolean isAuthenticated(Exchange exchange) {
        Object subject = exchange.getIn().getHeader("CamelAuthentication");
        return subject != null;
    }

    private boolean setAllowAnyRole(List<String> allowedRoles) {
        return allowedRoles.size() == 1 && "*".equals(allowedRoles.get(0));
    }

    public static Principal getUserPrincipal(Exchange exchange) {
        Subject subj = (Subject) exchange.getIn().getHeader("CamelAuthentication");
        return subj.getPrincipals().stream().findFirst().orElse(null);

    }

    private class AuthorizeDelegateProcess extends DelegateProcessor {

        AuthorizeDelegateProcess(Processor processor) {
            super(processor);
        }

        @Override
        public void process(Exchange exchange) throws Exception {
            beforeProcess(exchange);
            processNext(exchange);
        }

    }

}
