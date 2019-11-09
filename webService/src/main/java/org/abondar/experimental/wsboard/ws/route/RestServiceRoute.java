package org.abondar.experimental.wsboard.ws.route;

import org.abondar.experimental.wsboard.ws.security.JwtAuthorizationPolicy;
import org.abondar.experimental.wsboard.ws.util.I18nKeyUtil;
import org.apache.camel.CamelAuthorizationException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import javax.ws.rs.core.Response;
import java.util.Locale;

/**
 * Basic rest service route
 *
 * @author a.bondar
 */
public class RestServiceRoute extends RouteBuilder {

    @Autowired
    private MessageSource messageSource;

    @Override
    public void configure() throws Exception {

        JwtAuthorizationPolicy policy = new JwtAuthorizationPolicy();
        policy.setAllowAnyRole(true);
        policy.setAllowedRoles("*");

        onException(CamelAuthorizationException.class).handled(true)
                .transform().body((bdy, hdrs) -> {
                    String lang = (String) hdrs.get("Accept-language");
                    Locale locale = new Locale.Builder().setLanguage(lang).build();
                    return Response
                            .status(Response.Status.UNAUTHORIZED)
                            .entity(messageSource.getMessage(I18nKeyUtil.JWT_TOKEN_NOT_SET, null, locale))
                            .build();

                }
        );

        from("cxfrs:bean:jaxRsServer?bindingStyle=SimpleConsumer&synchronous=true")
                .routeId("restServiceRoute")
                .choice()
                .when(header("operationName").in("createUser", "loginUser",
                        "enterCode", "resetPassword", "updatePassword", "findUserByLogin"))
                .toD("direct:${headers.operationName}", false)
                .endChoice()
                .otherwise()
                .policy(policy)
                .toD("direct:${headers.operationName}", false);


    }
}
