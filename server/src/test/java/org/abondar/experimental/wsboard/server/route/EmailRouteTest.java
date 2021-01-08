package org.abondar.experimental.wsboard.server.route;


import org.abondar.experimental.wsboard.server.config.RouteConfig;
import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

@SpringBootTest(classes = RouteConfig.class)
@RunWith(CamelSpringBootRunner.class)
@ActiveProfiles("test")
@MockEndpointsAndSkip
public class EmailRouteTest {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Value("${email.admin}")
    private String emailAdmin;

    @Value("${email.from}")
    private String emailFrom;

    @EndpointInject(uri = "mock:{{email.server}}")
    private MockEndpoint mockEndpoint;

    @Test
    public void sendEmailRouteTest() throws Exception {

        producerTemplate.sendBodyAndHeaders("direct:sendEmail", new User(),
                Map.of("emailType", "createUser",
                        "To", "email",
                        "From", "Scrum Admin<" + emailAdmin + "@" + emailFrom + ">",
                        "contentType", "text/html"));
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "createUser");
        mockEndpoint.expectedMessageCount(1);

        mockEndpoint.reset();
    }

}
