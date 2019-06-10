package org.abondar.experimental.wsboard.email.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class EmailRoute extends RouteBuilder {

    private String emailRecipient;

    @Override
    public void configure() throws Exception {

        from("wsb:email").routeId("sendEmail")
                .transform().body((bdy, hdrs) -> {
            hdrs.put("To", emailRecipient);
            hdrs.put("From", "Scrum Admin<" + "{{email.admin}}@{{email.server}}");
            hdrs.put("contentType", "text/html");
            return bdy;
        })
                .doTry()
                .to("{{email.server}}")
                .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "${exception}")
                .endDoTry();
    }
}
