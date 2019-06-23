package org.abondar.experimental.wsboard.test.ws.route;

import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

@SpringBootTest(classes = WebScrumBoardApplication.class)
@RunWith(CamelSpringBootRunner.class)
@ActiveProfiles("test")
@MockEndpointsAndSkip
public class EmailRouteTest {

    @Autowired
    private ProducerTemplate producerTemplate;


    @EndpointInject(uri = "mock:{{email.server}}")
    private MockEndpoint mockEndpoint;

    @Test
    public void sendEmailRouteTest() throws Exception {

        producerTemplate.sendBodyAndHeaders("direct:sendEmail", new User(),
                Map.of("emailType", "createUser",
                        "To", "email",
                        "From", "Scrum Admin<" + "{{email.admin}}@{{email.server}}>",
                        "contentType", "text/html"));
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "createUser");
        mockEndpoint.expectedMessageCount(1);
        System.out.println(mockEndpoint.getReceivedExchanges());

        mockEndpoint.reset();
    }

}
