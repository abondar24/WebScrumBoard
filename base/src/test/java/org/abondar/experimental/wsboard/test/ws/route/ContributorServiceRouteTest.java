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
public class ContributorServiceRouteTest {


    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject(uri = "mock:sendEmail")
    private MockEndpoint mockEndpoint;

    private long someId = 7L;
    private boolean someState = true;
    private int somePagination = 0;

    @Test
    public void createContributorRouteTest() throws Exception {
        Object[] values = new Object[]{someId, someId, someState};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:createContributor", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void updateContributorRouteTest() throws Exception {
        Object[] values = new Object[]{someId,someId, someState, someState};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:updateContributor", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void findProjectOwnerRouteTest() throws Exception {
        Object[] values = new Object[]{someId};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:findProjectOwner", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void findProjectContributorsRouteTest() throws Exception {
        Object[] values = new Object[]{someId, somePagination, somePagination+1};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:findProjectContributors", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void countProjectContributorsRouteTest() throws Exception {
        Object[] values = new Object[]{someId};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:countProjectContributors", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void findContributorsByUserIdTest() throws Exception {
        Object[] values = new Object[]{someId, somePagination, somePagination+1};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:findContributorsByUserId", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void findProjectContributorTest() throws Exception {
        Object[] values = new Object[]{someId, someId};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:findProjectContributor", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void findContributorByLoginTest() throws Exception {
        Object[] values = new Object[]{someId, "sssss"};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:findContributorByLogin", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }
}
