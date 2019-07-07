package org.abondar.experimental.wsboard.ws.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

/**
 * Route sending email based on header
 *
 * @author a.bondar
 */
public class EmailRoute extends RouteBuilder {

    @Value("${email.server}")
    private String emailServer;

    @Value("${email.admin}")
    private String emailAdmin;

    private String emailFrom;

    @Override
    public void configure() throws Exception {

        from("direct:sendEmail").routeId("sendEmail")
                .transform().body((bdy, hdrs) -> {
            hdrs.put("From", "Scrum Admin<" + emailAdmin + "@" + "localhost" + ">");
            hdrs.put("contentType", "text/html");
            return bdy;
        })
                .choice()
                .when(header("emailType").isEqualTo("createUser"))
                .to("velocity:/velocity/createUser.html")
                .log("${body}")
                .endChoice()
                .when(header("emailType").isEqualTo("updateLogin"))
                .to("velocity:/velocity/updateLogin.html")
                .endChoice()
                .when(header("emailType").isEqualTo("updatePassword"))
                .to("velocity:/velocity/updatePassword.html")
                .endChoice()
                .when(header("emailType").isEqualTo("resetPassword"))
                .to("velocity:/velocity/resetPassword.html")
                .endChoice()
                .when(header("emailType").isEqualTo("deleteUser"))
                .to("velocity:/velocity/deleteUser.html")
                .end()
                .doTry()
                .to(emailServer)
                .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "${exception}")
                .endDoTry();
    }
}
