package org.abondar.experimental.wsboard.ws.security;


import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.ws.service.AuthService;
import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.rs.security.jose.common.JoseException;
import org.apache.cxf.rs.security.jose.jaxrs.JwtAuthenticationFilter;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.apache.cxf.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;

/**
 * Filter for token renewal after expiration
 *
 * @author a.bondar
 */
public class TokenRenewalFilter extends JwtAuthenticationFilter {

    private static Logger logger = LoggerFactory.getLogger(TokenRenewalFilter.class);

    private AuthService authService;


    @Override
    public void filter(ContainerRequestContext requestContext) {
        String encodedJwtToken;
        try {
            encodedJwtToken = getEncodedJwtToken(requestContext);
        } catch (JoseException ex) {
            logger.error(ex.getMessage());
            throw new JoseException(LogMessageUtil.JWT_TOKEN_NOT_SET);
        }

        JwtToken token = super.getJwtToken(encodedJwtToken);
        SecurityContext securityContext = configureSecurityContext(token);
        if (securityContext != null) {
            JAXRSUtils.getCurrentMessage().put(SecurityContext.class, securityContext);
            try {
                ((HttpServletResponse) JAXRSUtils.getCurrentMessage().get("HTTP.RESPONSE"))
                        .addCookie(new Cookie("X-JWT-AUTH", authService.renewToken(encodedJwtToken)));
            } catch (Base64Exception ex) {
                logger.error(ex.getMessage());

            }
        }

    }


    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

}
