package org.abondar.experimental.wsboard.ws.security;

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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthorizationPolicy implements AuthorizationPolicy {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthorizationPolicy.class);
    private List<String> allowedRoles = new ArrayList<>();
    private boolean allowAnyRole = false;

    @Override
    public void beforeWrap(RouteContext rc, ProcessorDefinition<?> pd) {
    }

    @Override
    public Processor wrap(RouteContext rc, Processor processor) {
        return new AuthorizeDelegateProcess(processor);
    }

    protected void beforeProcess(Exchange exchange) throws Exception {

        SecurityContext securityContext = getSecurityContext(exchange);
        if (null == securityContext) {
            CamelAuthorizationException authorizationException = new CamelAuthorizationException("Cannot find the SecurityContext instance.", exchange);
            throw authorizationException;
        }
        if (!(LoginSecurityContext.class.isAssignableFrom(securityContext.getClass()))) {
            LOGGER.info("the security context an instance of {}", securityContext.getClass());
            throw new CamelAuthorizationException("User was not recognized by security filters!", exchange);
        }
        Collection<String> userRoles = ((LoginSecurityContext) securityContext).getUserRoles().stream().map(Principal::getName).collect(Collectors.toSet());
        boolean isRolesAllowed = false;
        LOGGER.debug("User Prinicipal - {}", getUserPrincipal(exchange));
        LOGGER.debug("the security context contains {}", userRoles);
        if (allowAnyRole && isAuthenticated(exchange)) {
            if (userRoles.isEmpty()) {
                LOGGER.error("User {} not autheticated!", getUserPrincipal(exchange));
                throw new CamelAuthorizationException(String.format("User %s is not authenticated!", getUserPrincipal(exchange)), exchange);
            }
            return;
        }
        LOGGER.debug("Allowed roles {}", allowedRoles);
        for (String role : allowedRoles) {
            if (userRoles.contains(role)) {
                isRolesAllowed = true;
            }
        }
        LOGGER.debug("User {} authorized - {}", getUserPrincipal(exchange), isRolesAllowed);
        if (!isRolesAllowed) {
            throw new CamelAuthorizationException(allowedRoles.stream().collect(Collectors.joining(",", String.format("User %s not in roles: ", getUserPrincipal(exchange)), "!")), exchange);
        }

    }

    public List<String> getAllowedRoles() {
        return allowedRoles;
    }

    public boolean isAllowAnyRole() {
        return allowAnyRole;
    }

    public JwtAuthorizationPolicy allowAnyRole(final boolean value) {
        this.allowAnyRole = value;
        return this;
    }

    public void setAllowAnyRole(boolean allowAnyRole) {
        this.allowAnyRole = allowAnyRole;
    }

    public void setAllowedRoles(List<String> allowedRoles) {
        this.allowedRoles = allowedRoles;
        this.allowAnyRole = setAllowAnyRole(this.allowedRoles);
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

    public JwtAuthorizationPolicy allowedRoles(String... allowedRoles) {
        return allowedRoles(Arrays.asList(allowedRoles));
    }

    protected SecurityContext getSecurityContext(Exchange exchange) throws CamelAuthorizationException {
        try {
            SecurityContext sc = ((org.apache.cxf.message.Message) exchange.getIn().getHeader("CamelCxfMessage")).get(SecurityContext.class);
            return sc;
        } catch (Exception ex) {
            CamelAuthorizationException authorizationException = new CamelAuthorizationException("Cannot find the SecurityContext instance.", exchange, ex);
            throw authorizationException;
        }
    }

    protected boolean isAuthenticated(Exchange exchange) {
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
