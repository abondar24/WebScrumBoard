package org.abondar.experimental.wsboard.server.service;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.server.datamodel.Project;

import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.abondar.experimental.wsboard.server.datamodel.user.UserRole;
import org.abondar.experimental.wsboard.server.service.impl.ProjectServiceTestImpl;
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

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ProjectServiceTest {
    private static final String ENDPOINT = "local://wsboard_test_1";

    private final String projectName = "project";
    private final String startDate = "31/12/2018";

    @BeforeAll
    public static void beforeMethod() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        factory.setProvider(new JacksonJsonProvider());
        factory.setAddress(ENDPOINT);
        factory.setServiceBean(new ProjectServiceTestImpl());
        Server server = factory.create();
        server.start();
    }

    @Test
    public void createProjectTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", projectName);
        form.param("startDate", startDate);

        client.path("/project").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(200, res.getStatus());

        var prj = res.readEntity(Project.class);
        assertEquals(10, prj.getId());
    }

    @Test
    public void createProjectExistsTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", "exists");
        form.param("startDate", startDate);

        client.path("/project").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(302, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_EXISTS, msg);
    }

    @Test
    public void createProjectBlankNameTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", "");
        form.param("startDate", startDate);

        client.path("/project").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(206, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.BLANK_DATA, msg);
    }

    @Test
    public void createProjectIncorrectDateTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", projectName);
        form.param("startDate", "blabla");

        client.path("/project").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(206, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PARSE_DATE_FAILED, msg);
    }

    @Test
    public void updateProjectTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var prj = createProject();

        var form = new Form();
        form.param("repo", "test.repo.com");
        form.param("description","descr");

        client.path("/project/{id}",prj.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.put(form);
        assertEquals(200, res.getStatus());

        prj = res.readEntity(Project.class);
        assertEquals("test.repo.com", prj.getRepository());

    }

    @Test
    public void updateProjectNotFoundTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        createProject();

        var form = new Form();
        form.param("repo", "test.repo.com");

        client.path("/project/7").accept(MediaType.APPLICATION_JSON);

        var res = client.put(form);
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);

    }

    @Test
    public void updateProjectExistsFoundTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var prj = createProject();

        var form = new Form();
        form.param("name", prj.getName());

        client.path("/project/{id}",prj.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.put(form);
        assertEquals(302, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_EXISTS, msg);

    }

    @Test
    public void updateProjectDateAfterTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var prj = createProject();

        var form = new Form();
        form.param("id", String.valueOf(prj.getId()));
        form.param("isActive", "false");
        form.param("endDate", "31/05/2017");

        client.path("/project/{id}",prj.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.put(form);
        assertEquals(205, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.WRONG_END_DATE, msg);

    }


    @Test
    public void updateProjectDateNullTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var prj = createProject();

        var form = new Form();
        form.param("isActive", "false");
        form.param("endDate", "");

        client.path("/project/{id}",prj.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.put(form);
        assertEquals(206, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PARSE_DATE_FAILED, msg);

    }


    @Test
    public void updateProjectReactivateTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var prj = createProject();
        prj = updateProject(prj.getId());

        var form = new Form();
        form.param("isActive", "true");

        client.path("/project/{id}",prj.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.put(form);
        assertEquals(301, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_CANNOT_BE_REACTIVATED, msg);

    }


    @Test
    public void deleteProjectTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        var prj = createProject();

        client.path("/project/{id}",prj.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.delete();
        assertEquals(200, res.getStatus());
    }

    @Test
    public void deleteProjectNotExistsTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        createProject();

        client.path("/project/1000").accept(MediaType.APPLICATION_JSON);

        var res = client.delete();
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);
    }

    @Test
    public void findProjectTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        var prj = createProject();

        client.path("/project/{id}",prj.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(200, res.getStatus());

        var resPrj = res.readEntity(Project.class);
        assertEquals(prj.getName(), resPrj.getName());
    }


    @Test
    public void findProjectNotExistsTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        createProject();

        client.path("/project/7").accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);
    }

    @Test
    public void findUserProjectsTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        createProject();
        var usr = createUser();

        client.path("/project/user/{usrId}",usr).accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(200, res.getStatus());

        Collection<? extends Project> projects = client.getCollection(Project.class);
        assertEquals(2, projects.size());
    }

    @Test
    public void findUserProjectsNotFoundTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        createProject();
        createUser();

        client.path("/project/user/7").accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }

    private Project createProject() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", projectName);
        form.param("startDate", startDate);

        client.path("/project").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        return res.readEntity(Project.class);
    }

    private Project updateProject(long id) {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("isActive", "false");
        form.param("endDate", "31/05/2119");

        client.path("/project/{id}",id).accept(MediaType.APPLICATION_JSON);

        var res = client.put(form);

        return res.readEntity(Project.class);

    }

    private long createUser() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/project/user").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("login", "login");
        form.param("email", "email");
        form.param("firstName", "First");
        form.param("lastName", "Last");
        form.param("password", "pass");
        form.param("roles", UserRole.DEVELOPER.name());


        var resp = client.post(form);

        return resp.readEntity(User.class).getId();
    }

}
