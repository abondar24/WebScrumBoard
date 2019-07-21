package org.abondar.experimental.wsboard.ws.route;

import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.ws.security.JwtAuthorizationPolicy;
import org.apache.camel.CamelAuthorizationException;
import org.apache.camel.builder.RouteBuilder;

import javax.ws.rs.core.Response;

/**
 * Basic rest service route
 *
 * @author a.bondar
 */
public class RestServiceRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        JwtAuthorizationPolicy policy = new JwtAuthorizationPolicy();
        policy.setAllowAnyRole(true);
        policy.setAllowedRoles("*");

        onException(CamelAuthorizationException.class).handled(true)
                .transform().outBody(bdy-> Response.status(Response.Status.UNAUTHORIZED)
                .entity(LogMessageUtil.JWT_TOKEN_NOT_SET).build()
        );

        from("cxfrs:bean:jaxRsServer?bindingStyle=SimpleConsumer&synchronous=true")
                .routeId("restServiceRoute")
                .choice()
                .when(header("operationName").in("createUser","loginUser",
                        "enterCode","resetPassword","updatePassword","findUserByLogin"))
                .toD("direct:${headers.operationName}", false)
                .endChoice()
                .otherwise()
                .policy(policy)
                .toD("direct:${headers.operationName}", false);


    }
}
