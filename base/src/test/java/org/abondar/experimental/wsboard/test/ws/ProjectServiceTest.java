package org.abondar.experimental.wsboard.test.ws;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.test.ws.impl.ProjectServiceTestImpl;
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
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = WebScrumBoardApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ProjectServiceTest {
    private static Server server;
    private static String endpoint = "local://wsboard_test_1";

    private String projectName = "project";
    private String startDate = "31/12/2018";

    @BeforeAll
    public static void beforeMethod() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        factory.setProvider(new JacksonJsonProvider());
        factory.setAddress(endpoint);
        factory.setServiceBean(new ProjectServiceTestImpl());
        server = factory.create();
        server.start();
    }

    @Test
    public void createProjectTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", projectName);
        form.param("startDate", startDate);

        client.path("/project/create").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(200, res.getStatus());

        var prj = res.readEntity(Project.class);
        assertEquals(10, prj.getId());
    }

    @Test
    public void createProjectExistsTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", "exists");
        form.param("startDate", startDate);

        client.path("/project/create").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(302, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_EXISTS, msg);
    }

    @Test
    public void createProjectBlankNameTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", "");
        form.param("startDate", startDate);

        client.path("/project/create").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(206, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.BLANK_DATA, msg);
    }

    @Test
    public void createProjectIncorrectDateTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", projectName);
        form.param("startDate", "blabla");

        client.path("/project/create").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(206, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PARSE_DATE_FAILED, msg);
    }

    @Test
    public void updateProjectTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var prj = createProject();

        var form = new Form();
        form.param("id", String.valueOf(prj.getId()));
        form.param("repo", "test.repo.com");
        form.param("description","descr");

        client.path("/project/update").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(200, res.getStatus());

        prj = res.readEntity(Project.class);
        assertEquals("test.repo.com", prj.getRepository());

    }

    @Test
    public void updateProjectNotFoundTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        createProject();

        var form = new Form();
        form.param("id", "7");
        form.param("repo", "test.repo.com");

        client.path("/project/update").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);

    }

    @Test
    public void updateProjectExistsFoundTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var prj = createProject();

        var form = new Form();
        form.param("id", String.valueOf(prj.getId()));
        form.param("name", prj.getName());

        client.path("/project/update").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(302, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_EXISTS, msg);

    }

    @Test
    public void updateProjectDateAfterTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var prj = createProject();

        var form = new Form();
        form.param("id", String.valueOf(prj.getId()));
        form.param("isActive", "false");
        form.param("endDate", "31/05/2017");

        client.path("/project/update").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(205, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.WRONG_END_DATE, msg);

    }


    @Test
    public void updateProjectDateNullTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var prj = createProject();

        var form = new Form();
        form.param("id", String.valueOf(prj.getId()));
        form.param("isActive", "false");
        form.param("endDate", "");

        client.path("/project/update").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(206, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PARSE_DATE_FAILED, msg);

    }


    @Test
    public void updateProjectReactivateTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var prj = createProject();
        prj = updateProject(prj.getId());

        var form = new Form();
        form.param("id", String.valueOf(prj.getId()));
        form.param("isActive", "true");

        client.path("/project/update").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(301, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_CANNOT_BE_REACTIVATED, msg);

    }


    @Test
    public void deleteProjectTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        var prj = createProject();

        client.path("/project/delete").query("id", prj.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(200, res.getStatus());
    }

    @Test
    public void deleteProjectNotExistsTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        createProject();

        client.path("/project/delete").query("id", "1000").accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);
    }

    @Test
    public void findProjectTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        var prj = createProject();

        client.path("/project/find").query("id", prj.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(200, res.getStatus());

        var resPrj = res.readEntity(Project.class);
        assertEquals(prj.getName(), resPrj.getName());
    }


    @Test
    public void findProjectNotExistsTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        createProject();

        client.path("/project/find").query("id", "1000").accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);
    }

    private Project createProject() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", projectName);
        form.param("startDate", startDate);

        client.path("/project/create").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        return res.readEntity(Project.class);
    }

    private Project updateProject(long id) {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("id", String.valueOf(id));
        form.param("isActive", "false");
        form.param("endDate", "31/05/2119");

        client.path("/project/update").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);

        return res.readEntity(Project.class);

    }
}
