package org.abondar.experimental.wsboard.server.service;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.server.datamodel.Project;
import org.abondar.experimental.wsboard.server.datamodel.Sprint;

import org.abondar.experimental.wsboard.server.service.impl.SprintServiceTestImpl;
import org.abondar.experimental.wsboard.server.util.LogMessageUtil;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class SprintServiceTest {
    private static final String ENDPOINT = "local://wsboard_test_2";
    private final String sprintName = "test";
    private final String startDate = "29/06/2019";
    private final String endDate = "31/05/2119";

    @BeforeAll
    public static void beforeMethod() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        factory.setProvider(new JacksonJsonProvider());
        factory.setAddress(ENDPOINT);
        factory.setServiceBean(new SprintServiceTestImpl());
        Server server = factory.create();
        server.start();
    }

    @Test
    public void createSprintTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", sprintName);
        form.param("startDate", startDate);
        form.param("endDate", endDate);
        form.param("projectId",String.valueOf(createProject()));

        client.path("/sprint").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(200, res.getStatus());

        var sp = res.readEntity(Sprint.class);

        assertEquals(10, sp.getId());
        assertEquals("test", sp.getName());
    }

    @Test
    public void createSprintNameExistsTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", "exist");
        form.param("startDate", startDate);
        form.param("endDate", endDate);

        client.path("/sprint").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(302, res.getStatus());

        var msg = res.readEntity(String.class);

        assertEquals(LogMessageUtil.SPRINT_EXISTS, msg);
    }

    @Test
    public void createSprintEmptyNameTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", "");
        form.param("startDate", startDate);
        form.param("endDate", endDate);

        client.path("/sprint").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(205, res.getStatus());

        var msg = res.readEntity(String.class);

        assertEquals(LogMessageUtil.BLANK_DATA, msg);
    }

    @Test
    public void createSprintWrongEndDateTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", sprintName);
        form.param("startDate", startDate);
        form.param("endDate", "11/08/1109");

        client.path("/sprint").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(205, res.getStatus());

        var msg = res.readEntity(String.class);

        assertEquals(LogMessageUtil.WRONG_END_DATE, msg);
    }

    @Test
    public void createSprintEmptyDateTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", sprintName);
        form.param("startDate", startDate);
        form.param("endDate", "");

        client.path("/sprint").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(206, res.getStatus());

        var msg = res.readEntity(String.class);

        assertEquals(LogMessageUtil.PARSE_DATE_FAILED, msg);
    }

    @Test
    public void createSprintProjectNotFoundTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", sprintName);
        form.param("startDate", startDate);
        form.param("endDate", endDate);
        form.param("projectId","7");

        client.path("/sprint").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);

        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);
    }


    @Test
    public void updateSprintTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));


        var sp = createSprint(createProject());

        var form = new Form();
        form.param("name", "newName");

        client.path("/sprint/{id}",sp.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.put(form);
        assertEquals(200, res.getStatus());

        sp = res.readEntity(Sprint.class);

        assertEquals("newName", sp.getName());
    }

    @Test
    public void updateSprintWrongStartDateTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var sp = createSprint(createProject());

        var form = new Form();
        form.param("name", "newName");
        form.param("startDate", "blabla");

        client.path("/sprint/{id}",sp.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.put(form);
        assertEquals(204, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PARSE_DATE_FAILED, msg);
    }

    @Test
    public void updateSprintWrongEndDateTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var sp = createSprint(createProject());

        var form = new Form();
        form.param("name", "newName");
        form.param("endDate", "01/01/1997");

        client.path("/sprint/{id}",sp.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.put(form);
        assertEquals(205, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.WRONG_END_DATE, msg);
    }


    @Test
    public void updateSprintNotExistsTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        createSprint(createProject());

        var form = new Form();
        form.param("name", "newName");

        client.path("/sprint/7").accept(MediaType.APPLICATION_JSON);

        var res = client.put(form);
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.SPRINT_NOT_EXISTS, msg);
    }

    @Test
    public void updateSprintExistsTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var sp = createSprint(createProject());

        var form = new Form();
        form.param("name", sprintName);

        client.path("/sprint/{id}",sp.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.put(form);
        assertEquals(302, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.SPRINT_EXISTS, msg);
    }

    @Test
    public void updateSprintCurrentExistsTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var sp = createSprint(createProject());
        updateSprint(sp.getId(),true);

        var form = new Form();
        form.param("isCurrent","true");

        client.path("/sprint/{id}",sp.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.put(form);
        assertEquals(409, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.SPRINT_ACTIVE_EXISTS,msg);
    }



    @Test
    public void findSprintTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var sp = createSprint(createProject());

        client.path("/sprint/{id}",sp.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(200, res.getStatus());

        var found = res.readEntity(Sprint.class);
        assertEquals(sp.getId(), found.getId());
    }

    @Test
    public void findSprintNotFoundTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        createSprint(createProject());

        client.path("/sprint/7").accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var found = res.readEntity(String.class);
        assertEquals(LogMessageUtil.SPRINT_NOT_EXISTS, found);
    }

    @Test
    public void findCurrentSprintTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var pr = createProject();
        var sp = createSprint(pr);
        updateSprint(sp.getId(),true);


        client.path("/sprint/current/project/{prId}",pr).accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(200, res.getStatus());

        var curr = res.readEntity(Sprint.class);
        assertEquals(sp.getId(),curr.getId());
        assertTrue(curr.isCurrent());
    }

    @Test
    public void findNoCurrentSprintTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var pr = createProject();
        createSprint(pr);

        client.path("/sprint/current/project/{prId}",pr).accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(204, res.getStatus());

    }

    @Test
    public void findSprintsTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var prj = createProject();
        var sp = createSprint(prj);

        client.path("/sprint/all/project/{prId}",prj)
                .accept(MediaType.APPLICATION_JSON)
                .query("offset", 0)
                .query("limit", 2);

        var res = client.get();
        assertEquals(200, res.getStatus());

        Collection<? extends Sprint> sprints = client.getCollection(Sprint.class);

        assertEquals(2, sprints.size());
        assertEquals(sp.getId(), sprints.iterator().next().getId());
    }

    @Test
    public void findSprintsMinusOffsetTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var prj = createProject();
        var sp = createSprint(prj);

        client.path("/sprint/all/project/{prId}",prj)
                .accept(MediaType.APPLICATION_JSON)
                .query("offset", -1);

        var res = client.get();
        assertEquals(200, res.getStatus());

        Collection<? extends Sprint> sprints = client.getCollection(Sprint.class);

        assertEquals(5, sprints.size());
        assertEquals(sp.getId(), sprints.iterator().next().getId());
    }

    @Test
    public void findAllSprintsNotFoundTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        createSprint(createProject());

        client.path("/sprint/all/project/7")
                .accept(MediaType.APPLICATION_JSON)
                .query("offset", 6)
                .query("limit", 2);

        var res = client.get();
        assertEquals(404, res.getStatus());


    }


    @Test
    public void countSprintsTasksTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/sprint/count/project/7").accept(MediaType.APPLICATION_JSON);
        var res = client.get();
        assertEquals(200, res.getStatus());

        var count = res.readEntity(Integer.class);
        assertEquals(Integer.valueOf(7), count);

    }

    @Test
    public void deleteSprintTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var sp = createSprint(createProject());

        client.path("/sprint/{id}",sp.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.delete();
        assertEquals(200, res.getStatus());

    }

    @Test
    public void deleteSprintNotFoundTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        createSprint(createProject());

        client.path("/sprint/{id}",7).accept(MediaType.APPLICATION_JSON);

        var res = client.delete();
        assertEquals(404, res.getStatus());

        var found = res.readEntity(String.class);
        assertEquals(LogMessageUtil.SPRINT_NOT_EXISTS, found);
    }

    private long createProject() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", "prjName");
        form.param("startDate", "31/10/1999");

        client.path("/sprint/project").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        return res.readEntity(Project.class).getId();
    }

    private Sprint createSprint(long projectId) {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", sprintName);
        form.param("startDate", startDate);
        form.param("endDate", endDate);
        form.param("projectId",String.valueOf(projectId));


        client.path("/sprint").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        return res.readEntity(Sprint.class);

    }

    private void updateSprint(long sprintId,boolean isCurrent) {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("isCurrent", String.valueOf(isCurrent));

        client.path("/sprint/{id}",sprintId).accept(MediaType.APPLICATION_JSON);
        client.put(form);

    }
}
