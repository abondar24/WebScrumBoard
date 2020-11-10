package org.abondar.experimental.wsboard.project.route;


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

@SpringBootTest
@RunWith(CamelSpringBootRunner.class)
@ActiveProfiles("test")
@MockEndpoints
public class ProjectServiceRouteTest {
    @Autowired
    private ProducerTemplate producerTemplate;


    @EndpointInject(uri = "mock:sendEmail")
    private MockEndpoint mockEndpoint;

    private String name = "pr1";
    private String date = "01/01/1111";
    private long prId = 7L;

    @Test
    public void createProjectdRouteTest() throws Exception {
        Object[] values = new Object[]{name, date};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:createProject", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }


    @Test
    public void updateProjectRouteTest() throws Exception {
        Object[] values = new Object[]{prId, name, "repo", true, date,"descr"};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:updateProject", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void deleteProjectRouteTest() throws Exception {
        Object[] values = new Object[]{prId};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:deleteProject", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void findProjectByIdRouteTest() throws Exception {
        Object[] values = new Object[]{prId};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:findProjectById", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void findUserProjectsRouteTest() throws Exception {
        Object[] values = new Object[]{prId};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:findUserProjects", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

}

