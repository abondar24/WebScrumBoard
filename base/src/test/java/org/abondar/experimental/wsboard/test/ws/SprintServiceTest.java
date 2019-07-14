package org.abondar.experimental.wsboard.test.ws;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.datamodel.Sprint;
import org.abondar.experimental.wsboard.test.ws.impl.SprintServiceTestImpl;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = WebScrumBoardApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class SprintServiceTest {
    private static Server server;
    private static String endpoint = "local://wsboard_test_2";
    private String sprintName = "test";
    private String startDate = "29/06/2019";
    private String endDate = "31/05/2119";

    @BeforeAll
    public static void beforeMethod() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        factory.setProvider(new JacksonJsonProvider());
        factory.setAddress(endpoint);
        factory.setServiceBean(new SprintServiceTestImpl());
        server = factory.create();
        server.start();
    }

    @Test
    public void createSprintTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", sprintName);
        form.param("startDate", startDate);
        form.param("endDate", endDate);
        form.param("projectId","7");

        client.path("/sprint/create").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(200, res.getStatus());

        var sp = res.readEntity(Sprint.class);

        assertEquals(10, sp.getId());
        assertEquals("test", sp.getName());
    }

    @Test
    public void createSprintNameExistsTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", "exist");
        form.param("startDate", startDate);
        form.param("endDate", endDate);

        client.path("/sprint/create").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(302, res.getStatus());

        var msg = res.readEntity(String.class);

        assertEquals(LogMessageUtil.SPRINT_EXISTS, msg);
    }

    @Test
    public void createSprintEmptyNameTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", "");
        form.param("startDate", startDate);
        form.param("endDate", endDate);

        client.path("/sprint/create").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(205, res.getStatus());

        var msg = res.readEntity(String.class);

        assertEquals(LogMessageUtil.BLANK_DATA, msg);
    }

    @Test
    public void createSprintWrongEndDateTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", sprintName);
        form.param("startDate", startDate);
        form.param("endDate", "11/08/1109");

        client.path("/sprint/create").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(205, res.getStatus());

        var msg = res.readEntity(String.class);

        assertEquals(LogMessageUtil.WRONG_END_DATE, msg);
    }

    @Test
    public void createSprintEmptyDateTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", sprintName);
        form.param("startDate", startDate);
        form.param("endDate", "");

        client.path("/sprint/create").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(206, res.getStatus());

        var msg = res.readEntity(String.class);

        assertEquals(LogMessageUtil.PARSE_DATE_FAILED, msg);
    }

    @Test
    public void updateSprintTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var sp = createSprint();

        var form = new Form();
        form.param("id", String.valueOf(sp.getId()));
        form.param("name", "newName");

        client.path("/sprint/update").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(200, res.getStatus());

        sp = res.readEntity(Sprint.class);

        assertEquals("newName", sp.getName());
    }

    @Test
    public void updateSprintWrongStartDateTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var sp = createSprint();

        var form = new Form();
        form.param("id", String.valueOf(sp.getId()));
        form.param("name", "newName");
        form.param("startDate", "blabla");

        client.path("/sprint/update").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(204, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PARSE_DATE_FAILED, msg);
    }

    @Test
    public void updateSprintWrongEndDateTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var sp = createSprint();

        var form = new Form();
        form.param("id", String.valueOf(sp.getId()));
        form.param("name", "newName");
        form.param("endDate", "01/01/1997");

        client.path("/sprint/update").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(205, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.WRONG_END_DATE, msg);
    }


    @Test
    public void updateSprintNotExistsTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        createSprint();

        var form = new Form();
        form.param("id", "7");

        client.path("/sprint/update").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.SPRINT_NOT_EXISTS, msg);
    }

    @Test
    public void updateSprintExistsTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var sp = createSprint();

        var form = new Form();
        form.param("id", String.valueOf(sp.getId()));
        form.param("name", sprintName);

        client.path("/sprint/update").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(302, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.SPRINT_EXISTS, msg);
    }


    @Test
    public void findSprintTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var sp = createSprint();

        client.path("/sprint/find").query("id", sp.getId());

        var res = client.get();
        assertEquals(200, res.getStatus());

        var found = res.readEntity(Sprint.class);
        assertEquals(sp.getId(), found.getId());
    }

    @Test
    public void findSprintNotFoundTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        createSprint();

        client.path("/sprint/find").query("id", 7);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var found = res.readEntity(String.class);
        assertEquals(LogMessageUtil.SPRINT_NOT_EXISTS, found);
    }

    @Test
    public void findAllSprintsTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var sp = createSprint();

        client.path("/sprint/find_all")
                .query("offset", 0)
                .query("limit", 2);

        var res = client.get();
        assertEquals(200, res.getStatus());

        Collection<? extends Sprint> sprints = client.getCollection(Sprint.class);

        assertEquals(2, sprints.size());
        assertEquals(sp.getId(), sprints.iterator().next().getId());
    }

    @Test
    public void findAllSprintsMinusOffsetTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var sp = createSprint();

        client.path("/sprint/find_all").query("offset", -1);

        var res = client.get();
        assertEquals(200, res.getStatus());

        Collection<? extends Sprint> sprints = client.getCollection(Sprint.class);

        assertEquals(5, sprints.size());
        assertEquals(sp.getId(), sprints.iterator().next().getId());
    }

    @Test
    public void findAllSprintsNotFoundTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        createSprint();

        client.path("/sprint/find_all")
                .query("offset", 6)
                .query("limit", 2);

        var res = client.get();
        assertEquals(404, res.getStatus());


    }

    @Test
    public void deleteSprintTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var sp = createSprint();

        client.path("/sprint/delete").query("id", sp.getId());

        var res = client.get();
        assertEquals(200, res.getStatus());

    }

    @Test
    public void deleteSprintNotFoundTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        createSprint();

        client.path("/sprint/delete").query("id", 7);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var found = res.readEntity(String.class);
        assertEquals(LogMessageUtil.SPRINT_NOT_EXISTS, found);
    }

    private Sprint createSprint() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", sprintName);
        form.param("startDate", startDate);
        form.param("endDate", endDate);
        form.param("projectId","7");


        client.path("/sprint/create").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        return res.readEntity(Sprint.class);

    }
}
