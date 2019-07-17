package org.abondar.experimental.wsboard.test.ws.route;

import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.cxf.message.MessageContentsList;
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
public class SprintServiceRouteTest {
    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject(uri = "mock:sendEmail")
    private MockEndpoint mockEndpoint;

    private long someId = 7L;
    private String someName = "mame";
    private String date = "01/01/1111";

    @Test
    public void createSprintRouteTest() throws Exception {
        Object[] values = new Object[]{someName, date, date,someId};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:createSprint", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void updateSprintRouteTest() throws Exception {
        Object[] values = new Object[]{someId, someName, date, date};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:updateSprint", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void getSprintByIdRouteTest() throws Exception {
        Object[] values = new Object[]{someId};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:getSprintById", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void getSprintsRouteTest() throws Exception {
        Object[] values = new Object[]{someId,0, 3};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:getSprints", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void deleteSprintRouteTest() throws Exception {
        Object[] values = new Object[]{someId};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:deleteSprint", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }
}
