package org.abondar.experimental.wsboard.ws.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class UserEmailRoute extends RouteBuilder {


    @Override
    public void configure() throws Exception {
        from("cxfrs:bean:jaxRsServer?bindingStyle=SimpleConsumer")
                .routeId("userEmailRoute")
                .log(simple("${header.operationName}").toString())
                .toD("direct:${header.operationName}");

        from("direct:createUser").routeId("createUser")
                .transform()
                .body((bdy, hdrs) -> hdrs.put("emailType", "createUser"))
                .to("direct:sendEmail");

        from("direct:updateLogin").routeId("updateLogin")
                .transform()
                .body((bdy, hdrs) -> hdrs.put("emailType", "updateLogin"))
                .to("direct:sendEmail");

        from("direct:updatePassword").routeId("updatePassword")
                .transform()
                .body((bdy, hdrs) -> hdrs.put("emailType", "updatePassword"))
                .to("direct:sendEmail");

        from("direct:deleteUser").routeId("deleteUser")
                .transform()
                .body((bdy, hdrs) -> hdrs.put("emailType", "deleteUser"))
                .to("direct:sendEmail");

    }
}
