package org.abondar.experimental.wsboard.test.ws.route;

import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

@SpringBootTest(classes = WebScrumBoardApplication.class)
@RunWith(CamelSpringBootRunner.class)
@ActiveProfiles("test")
@MockEndpoints
public class UserEmailRouteTest {

    @Autowired
    private ProducerTemplate producerTemplate;


    @EndpointInject(uri = "mock:sendEmail")
    private MockEndpoint mockEndpoint;

    @Test
    public void createUserRouteTest() throws Exception {
        producerTemplate.sendBodyAndHeaders("direct:createUser", new User(),
                Map.of("emailType", "createUser"));
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "createUser");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void updateLoginRouteTest() throws Exception {
        producerTemplate.sendBodyAndHeaders("direct:updateLogin", new User(),
                Map.of("emailType", "updateLogin"));
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "updateLogin");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void updatePasswordRouteTest() throws Exception {
        producerTemplate.sendBodyAndHeaders("direct:updatePassword", new User(),
                Map.of("emailType", "updatePassword"));
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "updatePassword");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void deleteUserRouteTest() throws Exception {
        producerTemplate.sendBodyAndHeaders("direct:deleteUser", 7,
                Map.of("emailType", "deleteUser"));
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "deleteUser");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }
}
