package org.abondar.experimental.wsboard.test.ws.route;

import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

@SpringBootTest(classes = WebScrumBoardApplication.class)
@RunWith(CamelSpringBootRunner.class)
@ActiveProfiles("test")
public class UserEmailRouteTest {

    @EndpointInject(uri = "direct:createUser")
    private ProducerTemplate producerTemplate;

    @Autowired
    private CamelContext camelContext;

    @EndpointInject(uri = "mock:sendEmail")
    private MockEndpoint mockEndpoint;

    @Before
    public void setup() throws Exception {

        camelContext.getRouteDefinition("userEmailRoute")
                .autoStartup(true)
                .adviceWith(camelContext, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        interceptSendToEndpoint("log*")
                                .skipSendToOriginalEndpoint()
                                .to("mock:sendEmail");
                    }
                });
    }


    @Test
    public void createUserRouteTest() throws Exception {
        mockEndpoint.expectedMessageCount(1);
        producerTemplate.sendBodyAndHeaders("direct:createUser", new User(),
                Map.of("emailType", "createUser"));
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "createUser");
        mockEndpoint.reset();
    }


}
