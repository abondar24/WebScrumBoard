package org.abondar.experimental.wsboard.common.service;


import org.abondar.experimental.wsboard.common.exception.TokenExpiredException;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.rs.security.jose.common.JoseException;
import org.apache.cxf.rs.security.jose.jaxrs.JwtAuthenticationFilter;
import org.apache.cxf.rs.security.jose.jaxrs.JwtTokenSecurityContext;
import org.apache.cxf.rs.security.jose.jwt.JwtException;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.apache.cxf.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;

/**
 * Filter for token renewal after expiration
 *
 * @author a.bondar
 */
public class TokenRenewalFilter extends JwtAuthenticationFilter {

    private static Logger logger = LoggerFactory.getLogger(TokenRenewalFilter.class);


    @Override
    public void filter(ContainerRequestContext requestContext) {
        String encodedJwtToken;
        try {
            encodedJwtToken = getEncodedJwtToken(requestContext);
        } catch (JoseException ex) {
            logger.error(ex.getMessage());
            return;

        }

        try {
            JwtToken token = super.getJwtToken(encodedJwtToken);
            SecurityContext securityContext = configureSecurityContext(token);

            if (securityContext != null) {
                JAXRSUtils.getCurrentMessage().put(SecurityContext.class, securityContext);
            }
        } catch (JwtException ex){
            logger.error(ex.getMessage());
            throw new TokenExpiredException(ex.getMessage(),requestContext.getHeaderString("Accept-Language"));
        }


    }

    @Override
    protected SecurityContext configureSecurityContext(JwtToken jwt) {
        return new JwtTokenSecurityContext(jwt, "roles");
    }

}
