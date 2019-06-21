package org.abondar.experimental.wsboard.ws.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

/**
 * Route sending email based on header
 */
public class EmailRoute extends RouteBuilder {


    @Override
    public void configure() throws Exception {

        from("direct:sendEmail").routeId("sendEmail")
                .log(simple("${header.operationName}").toString())
                .transform().body((bdy, hdrs) -> {
            hdrs.put("To", hdrs.get("email"));
            hdrs.put("From", "Scrum Admin<" + "{{email.admin}}@{{email.server}}>");
            hdrs.put("contentType", "text/html");
            return bdy;
        })
                .choice()
                .when(header("emailType").isEqualTo("createUser"))
                .to("velocity:/velocity/createUser.html")
                .when(header("emailType").isEqualTo("updateLogin"))
                .to("velocity:/velocity/updateLogin.html")
                .when(header("emailType").isEqualTo("updatePassword"))
                .to("velocity:/velocity/updatePassword.html")
                .when(header("emailType").isEqualTo("deleteUser"))
                .to("velocity:/velocity/deleteUser.html")
                .doTry()
                .to("{{email.server}}")
                .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "${exception}")
                .endDoTry();
    }
}
