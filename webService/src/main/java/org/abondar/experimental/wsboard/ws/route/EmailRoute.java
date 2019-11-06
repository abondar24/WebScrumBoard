package org.abondar.experimental.wsboard.ws.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

import static org.abondar.experimental.wsboard.ws.util.RouteConstantUtil.EMAIL_TYPE_HEADER;

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
                .choice()
                .when(header(EMAIL_TYPE_HEADER).isEqualTo("createUser"))

                .to("velocity:/velocity/createUser.html?encoding=UTF-8")
                .removeHeader("email")
                .removeHeader("firstName")
                .removeHeader("lastName")
                .removeHeader("login")
                .removeHeader("password")
                .removeHeader("roles")
                .endChoice()
                .when(header(EMAIL_TYPE_HEADER).isEqualTo("updateLogin"))
                .to("velocity:/velocity/updateLogin.html")
                .removeHeader("firstName")
                .removeHeader("login")
                .removeHeader("id")
                .endChoice()
                .when(header(EMAIL_TYPE_HEADER).isEqualTo("updatePassword"))
                .to("velocity:/velocity/updatePassword.html")
                .removeHeader("login")
                .removeHeader("id")
                .removeHeader("newPassword")
                .removeHeader("oldPassword")
                .endChoice()
                .when(header(EMAIL_TYPE_HEADER).isEqualTo("resetPassword"))
                .to("velocity:/velocity/resetPassword.html")
                .removeHeader("login")
                .removeHeader("id")
                .removeHeader("code")
                .endChoice()
                .when(header(EMAIL_TYPE_HEADER).isEqualTo("deleteUser"))
                .to("velocity:/velocity/deleteUser.html")
                .removeHeader("id")
                .end()
                .doTry()
                .to(emailServer)
                .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "${exception}")
                .endDoTry();
    }
}
