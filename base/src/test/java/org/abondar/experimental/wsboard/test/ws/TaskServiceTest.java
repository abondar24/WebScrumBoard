package org.abondar.experimental.wsboard.test.ws;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.task.Task;
import org.abondar.experimental.wsboard.datamodel.task.TaskState;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.abondar.experimental.wsboard.test.ws.impl.AuthServiceTestImpl;
import org.abondar.experimental.wsboard.test.ws.impl.ContributorServiceTestImpl;
import org.abondar.experimental.wsboard.test.ws.impl.ProjectServiceTestImpl;
import org.abondar.experimental.wsboard.test.ws.impl.SprintServiceTestImpl;
import org.abondar.experimental.wsboard.test.ws.impl.TaskServiceTestImpl;
import org.abondar.experimental.wsboard.test.ws.impl.UserServiceTestImpl;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class TaskServiceTest {
    private Logger logger = LoggerFactory.getLogger(TaskServiceTest.class);

    private static Server server;
    private static String endpoint = "local://wsboard_test_4";

    @BeforeAll
    public static void beforeMethod() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        factory.setProvider(new JacksonJsonProvider());
        factory.setAddress(endpoint);
        factory.setServiceBeanObjects(new TaskServiceTestImpl(),
                new ContributorServiceTestImpl(),
                new UserServiceTestImpl(new AuthServiceTestImpl()),
                new ProjectServiceTestImpl(),
                new SprintServiceTestImpl());
        server = factory.create();
        server.start();
    }


    @Test
    public void createTaskTest() {
        logger.info("create task test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/create").accept(MediaType.APPLICATION_JSON);

        createUser();
        createProject();
        var ctrId = createContributor();

        var form = new Form();
        form.param("ctrId", String.valueOf(ctrId));
        form.param("startDate", "31/12/2119");
        form.param("devOps", "false");

        var resp = client.post(form);
        assertEquals(200, resp.getStatus());

        var res = resp.readEntity(Task.class);
        assertEquals(TaskState.CREATED, res.getTaskState());
    }

    @Test
    public void createTaskWrongDateTest() {
        logger.info("create task contributor wrong date test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/create").accept(MediaType.APPLICATION_JSON);

        createUser();
        createProject();
        var ctrId = createContributor();

        var form = new Form();
        form.param("ctrId", String.valueOf(ctrId));
        form.param("startDate", "bla");
        form.param("devOps", "false");

        var resp = client.post(form);
        assertEquals(206, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.PARSE_DATE_FAILED, msg);
    }

    @Test
    public void createTaskContributorNotExistsTest() {
        logger.info("create task contributor not exists test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/create").accept(MediaType.APPLICATION_JSON);


        var form = new Form();
        form.param("ctrId", "7");
        form.param("startDate", "31/12/2119");
        form.param("devOps", "false");

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS, msg);
    }

    @Test
    public void updateTaskTest() {
        logger.info("update task test");

        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", String.valueOf(taskId));
        form.param("ctrId", String.valueOf(ctrId));
        form.param("startDate", "31/12/2119");
        form.param("devOps", "false");
        form.param("storyPoints", "8");

        var resp = client.post(form);
        assertEquals(200, resp.getStatus());

        var res = resp.readEntity(Task.class);
        assertEquals(taskId, res.getId());
    }

    @Test
    public void updateTaskNotFoundTest() {
        logger.info("update task  not found test");

        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", "7");
        form.param("ctrId", String.valueOf(ctrId));
        form.param("startDate", "31/12/2119");
        form.param("devOps", "false");
        form.param("storyPoints", "8");

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var res = resp.readEntity(Task.class);
        assertEquals(taskId, res.getId());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.TASK_NOT_EXISTS, msg);
    }

    @Test
    public void updateTaskContributorNotExistsTest() {
        logger.info("update task contributor not found test");

        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", "7");
        form.param("ctrId", String.valueOf(ctrId));
        form.param("startDate", "31/12/2119");
        form.param("devOps", "false");
        form.param("storyPoints", "8");

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var res = resp.readEntity(Task.class);
        assertEquals(taskId, res.getId());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS, msg);
    }





    private long createUser() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/task/create_user").accept(MediaType.APPLICATION_JSON);

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

    private long createProject() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", "prjName");
        form.param("startDate", "31/10/1999");

        client.path("/task/create_project").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        return res.readEntity(Project.class).getId();
    }


    private long createContributor() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/create_contributor").accept(MediaType.APPLICATION_JSON);


        var resp = client.get();

        return resp.readEntity(Contributor.class).getId();
    }

    private long createTask(long ctrId) {

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/create").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("ctrId", String.valueOf(ctrId));
        form.param("startDate", "31/12/2119");
        form.param("devOps", "false");

        var resp = client.post(form);

        return resp.readEntity(Task.class).getId();
    }
}
