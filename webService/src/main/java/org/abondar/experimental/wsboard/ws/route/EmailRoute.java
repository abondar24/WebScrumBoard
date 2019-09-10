package org.abondar.experimental.wsboard.ws.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

import static org.abondar.experimental.wsboard.ws.route.RouteConstantUtil.EMAIL_TYPE_HEADER;

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

    @Value("${email.from}")
    private String emailFrom;

    @Override
    public void configure() throws Exception {

        from("direct:sendEmail").routeId("sendEmail")
                .transform().body((bdy, hdrs) -> {
            hdrs.put("From", "Scrum Admin<" + emailAdmin + "@" + emailFrom + ">");
            hdrs.put("contentType", "text/html");
            return bdy;
        })
                //TODO: remove headers with user data
                .choice()
                .when(header(EMAIL_TYPE_HEADER).isEqualTo("createUser"))
                .to("velocity:/velocity/createUser.html")
                .endChoice()
                .when(header(EMAIL_TYPE_HEADER).isEqualTo("updateLogin"))
                .to("velocity:/velocity/updateLogin.html")
                .endChoice()
                .when(header(EMAIL_TYPE_HEADER).isEqualTo("updatePassword"))
                .to("velocity:/velocity/updatePassword.html")
                .endChoice()
                .when(header(EMAIL_TYPE_HEADER).isEqualTo("resetPassword"))
                .to("velocity:/velocity/resetPassword.html")
                .endChoice()
                .when(header(EMAIL_TYPE_HEADER).isEqualTo("deleteUser"))
                .to("velocity:/velocity/deleteUser.html")
                .end()
                .doTry()
                .to(emailServer)
                .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "${exception}")
                .endDoTry();
    }
}
