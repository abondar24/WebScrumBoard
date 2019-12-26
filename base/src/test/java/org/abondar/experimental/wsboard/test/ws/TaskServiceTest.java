package org.abondar.experimental.wsboard.test.ws;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.Sprint;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = WebScrumBoardApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class TaskServiceTest {
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
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/create").accept(MediaType.APPLICATION_JSON);

        createUser();
        createProject();
        var ctrId = createContributor();

        var form = new Form();
        form.param("ctrId", String.valueOf(ctrId));
        form.param("startDate", "31/12/2119");
        form.param("devOps", "false");
        form.param("taskName", "name");
        form.param("taskDescription", "descr");


        var resp = client.post(form);
        assertEquals(200, resp.getStatus());

        var res = resp.readEntity(Task.class);
        assertEquals(TaskState.CREATED, res.getTaskState());
    }

    @Test
    public void createTaskWrongDateTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/create").accept(MediaType.APPLICATION_JSON);

        createUser();
        createProject();
        var ctrId = createContributor();

        var form = new Form();
        form.param("ctrId", String.valueOf(ctrId));
        form.param("startDate", "bla");
        form.param("devOps", "false");
        form.param("taskName", "name");
        form.param("taskDescription", "descr");

        var resp = client.post(form);
        assertEquals(206, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.PARSE_DATE_FAILED, msg);
    }

    @Test
    public void createTaskContributorNotExistsTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/create").accept(MediaType.APPLICATION_JSON);


        var form = new Form();
        form.param("ctrId", "7");
        form.param("startDate", "31/12/2119");
        form.param("devOps", "false");
        form.param("taskName", "name");
        form.param("taskDescription", "descr");

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS, msg);
    }

    @Test
    public void updateTaskTest() {
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
        form.param("taskName", "newName");
        form.param("taskDescription", "newDescr");

        var resp = client.post(form);
        assertEquals(200, resp.getStatus());

        var res = resp.readEntity(Task.class);
        assertEquals(taskId, res.getId());
    }

    @Test
    public void updateTaskNotFoundTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", "7");
        form.param("ctrId", String.valueOf(ctrId));
        form.param("startDate", "31/12/2119");
        form.param("devOps", "false");
        form.param("storyPoints", "8");
        form.param("taskName", "newName");
        form.param("taskDescription", "newDescr");

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.TASK_NOT_EXISTS, msg);
    }

    @Test
    public void updateTaskContributorNotExistsTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", String.valueOf(taskId));
        form.param("ctrId", "7");
        form.param("startDate", "31/12/2119");
        form.param("devOps", "false");
        form.param("storyPoints", "8");
        form.param("taskName", "newName");
        form.param("taskDescription", "newDescr");

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS, msg);
    }


    @Test
    public void updateTaskSprintTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);
        var sprintId = createSprint();

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update_sprint").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", String.valueOf(taskId));
        form.param("sprintId", String.valueOf(sprintId));

        var resp = client.post(form);
        assertEquals(200, resp.getStatus());

        var res = resp.readEntity(Task.class);
        assertEquals(sprintId, res.getSprintId());
    }

    @Test
    public void updateTasksSprintTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);
        var sprintId = createSprint();

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update_tasks_sprint").accept(MediaType.APPLICATION_JSON)
                .query("id", taskId)
                .query("id", 1L)
                .query("id", 2L)
                .query("sprintId",sprintId);

        var resp = client.put(null);
        assertEquals(200, resp.getStatus());
    }

    @Test
    public void updateTaskSprintTaskNotFoundTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        createTask(ctrId);
        var sprintId = createSprint();

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update_sprint").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", "7");
        form.param("sprintId", String.valueOf(sprintId));

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.TASK_NOT_EXISTS, msg);
    }


    @Test
    public void updateTaskSprintNotFoundTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);
        createSprint();

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update_sprint").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", String.valueOf(taskId));
        form.param("sprintId", "19");

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.SPRINT_NOT_EXISTS, msg);
    }

    @Test
    public void updateTaskStateTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update_state").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", String.valueOf(taskId));
        form.param("state", TaskState.IN_DEVELOPMENT.name());

        var resp = client.post(form);
        assertEquals(200, resp.getStatus());

        var res = resp.readEntity(Task.class);
        assertEquals(TaskState.CREATED, res.getPrevState());
        assertEquals(TaskState.IN_DEVELOPMENT, res.getTaskState());

    }

    @Test
    public void updateTaskStateTaskNotFoundTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update_state").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", "7");
        form.param("state", TaskState.IN_DEVELOPMENT.name());

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.TASK_NOT_EXISTS, msg);

    }

    @Test
    public void updateTaskStateUnknownTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update_state").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", String.valueOf(taskId));
        form.param("state", "test");

        var resp = client.post(form);
        assertEquals(400, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.TASK_STATE_UNKNOWN, msg);

    }

    @Test
    public void updateTaskStateCreatedTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update_state").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", String.valueOf(taskId));
        form.param("state", TaskState.CREATED.name());

        var resp = client.post(form);
        assertEquals(201, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.TASK_ALREADY_CREATED, msg);

    }

    @Test
    public void updateTaskStateCompletedTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update_state").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", String.valueOf(taskId));
        form.param("state", TaskState.COMPLETED.name());


        var resp = client.post(form);
        assertEquals(200, resp.getStatus());

        var res = resp.readEntity(Task.class);
        assertNotNull(res.getEndDate());

    }

    @Test
    public void updateTaskStateAlreadyCompletedTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);
        updateTaskState(taskId, TaskState.COMPLETED);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update_state").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", String.valueOf(taskId));
        form.param("state", TaskState.IN_DEVELOPMENT.name());


        var resp = client.post(form);
        assertEquals(302, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.TASK_ALREADY_COMPLETED, msg);

    }

    @Test
    public void updateTaskStateInDeploymentTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update_state").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", String.valueOf(taskId));
        form.param("state", TaskState.IN_DEPLOYMENT.name());


        var resp = client.post(form);
        assertEquals(204, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.TASK_DEV_OPS_NOT_ENABLED, msg);

    }

    @Test
    public void updateTaskStatePausedTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);
        updateTaskState(taskId, TaskState.PAUSED);


        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update_state").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", String.valueOf(taskId));
        form.param("state", TaskState.IN_TEST.name());


        var resp = client.post(form);
        assertEquals(409, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.TASK_WRONG_STATE_AFTER_PAUSE, msg);

    }

    @Test
    public void updateTaskStateMoveNotAvailableTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update_state").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", String.valueOf(taskId));
        form.param("state", TaskState.IN_CODE_REVIEW.name());


        var resp = client.post(form);
        assertEquals(501, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.TASK_MOVE_NOT_AVAILABLE, msg);

    }

    @Test
    public void updateTaskStateRedirectTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update_state").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", String.valueOf(taskId));
        form.param("state", TaskState.IN_TEST.name());


        var resp = client.post(form);
        assertEquals(202, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.TASK_CONTRIBUTOR_UPDATE, msg);

    }

    @Test
    public void deleteTaskTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/delete").accept(MediaType.APPLICATION_JSON).query("id", taskId);

        var resp = client.delete();
        assertEquals(200, resp.getStatus());

    }

    @Test
    public void deleteTaskNotFoundTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/delete").accept(MediaType.APPLICATION_JSON).query("id", 8);

        var resp = client.delete();
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.TASK_NOT_EXISTS, msg);
    }

    @Test
    public void findTaskTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/find").accept(MediaType.APPLICATION_JSON).query("id", taskId);

        var resp = client.get();
        assertEquals(200, resp.getStatus());

        var res = resp.readEntity(Task.class);
        assertEquals(taskId, res.getId());

    }

    @Test
    public void findTaskNotFoundTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/find").accept(MediaType.APPLICATION_JSON).query("id", 8);

        var resp = client.get();
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.TASK_NOT_EXISTS, msg);

    }

    @Test
    public void findTaskForProjectTest() {
        var prId = createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/find_project_tasks").accept(MediaType.APPLICATION_JSON)
                .query("prId", prId)
                .query("offset", 0)
                .query("limit", 2)
                .query("all","true");

        var resp = client.get();
        assertEquals(200, resp.getStatus());

        Collection<? extends Task> tasks = client.getCollection(Task.class);
        assertEquals(1, tasks.size());
        assertEquals(taskId, tasks.iterator().next().getId());
    }

    @Test
    public void findTaskForProjectEmptyResultTest() {
        var prId = createProject();
        createUser();
        var ctrId = createContributor();
        createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/find_project_tasks").accept(MediaType.APPLICATION_JSON)
                .query("prId", prId)
                .query("offset", 1)
                .query("limit", 1)
                .query("all","true");


        var resp = client.get();
        assertEquals(204, resp.getStatus());
    }

    @Test
    public void findTaskForProjectNotFoundTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/find_project_tasks").accept(MediaType.APPLICATION_JSON)
                .query("prId", 8)
                .query("offset", 0)
                .query("limit", 2)
                .query("all","true");


        var resp = client.get();
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);
    }


    @Test
    public void findTaskForContributorTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/find_contributor_tasks").accept(MediaType.APPLICATION_JSON)
                .query("ctrId", ctrId)
                .query("offset", 0)
                .query("limit", 2);

        var resp = client.get();
        assertEquals(200, resp.getStatus());

        Collection<? extends Task> tasks = client.getCollection(Task.class);
        assertEquals(1, tasks.size());
        assertEquals(taskId, tasks.iterator().next().getId());
    }

    @Test
    public void findTaskForContributorEmptyResultTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/find_contributor_tasks").accept(MediaType.APPLICATION_JSON)
                .query("ctrId", ctrId)
                .query("offset", 1)
                .query("limit", 1);

        var resp = client.get();
        assertEquals(204, resp.getStatus());
    }

    @Test
    public void findTaskForContributorNotFoundTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/find_contributor_tasks").accept(MediaType.APPLICATION_JSON)
                .query("ctrId", 8)
                .query("offset", 0)
                .query("limit", 2);

        var resp = client.get();
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS, msg);
    }


    @Test
    public void findTaskForUserTest() {
        createProject();
        var usrId = createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/find_user_tasks").accept(MediaType.APPLICATION_JSON)
                .query("usrId", usrId)
                .query("offset", 0)
                .query("limit", 2);

        var resp = client.get();
        assertEquals(200, resp.getStatus());

        Collection<? extends Task> tasks = client.getCollection(Task.class);
        assertEquals(1, tasks.size());
        assertEquals(taskId, tasks.iterator().next().getId());
    }

    @Test
    public void findTaskForUserEmptyResultTest() {
        createProject();
        var usrId = createUser();
        var ctrId = createContributor();
        createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/find_user_tasks").accept(MediaType.APPLICATION_JSON)
                .query("usrId", usrId)
                .query("offset", 1)
                .query("limit", 1);

        var resp = client.get();
        assertEquals(204, resp.getStatus());
    }

    @Test
    public void findTaskForUserNotFoundTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/find_user_tasks").accept(MediaType.APPLICATION_JSON)
                .query("usrId", 8)
                .query("offset", 0)
                .query("limit", 2);

        var resp = client.get();
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }

    @Test
    public void countUserTasksTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/count_user_tasks")
                .accept(MediaType.APPLICATION_JSON)
                .query("userId", 7);

        var res = client.get();
        assertEquals(200, res.getStatus());

        var count = res.readEntity(Integer.class);
        assertEquals(Integer.valueOf(7), count);

    }

    @Test
    public void countContributorTasksTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/count_contributor_tasks")
                .accept(MediaType.APPLICATION_JSON)
                .query("contributorId", 7);

        var res = client.get();
        assertEquals(200, res.getStatus());

        var count = res.readEntity(Integer.class);
        assertEquals(Integer.valueOf(7), count);

    }

    @Test
    public void findTaskForSprintTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        var taskId = createTask(ctrId);
        var spId = createSprint();

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/find_sprint_tasks").accept(MediaType.APPLICATION_JSON)
                .query("spId", spId)
                .query("offset", 0)
                .query("limit", 2);

        var resp = client.get();
        assertEquals(200, resp.getStatus());

        Collection<? extends Task> tasks = client.getCollection(Task.class);
        assertEquals(1, tasks.size());
        assertEquals(taskId, tasks.iterator().next().getId());
    }


    @Test
    public void findTaskForSprintEmptyResultTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        createTask(ctrId);
        var spId = createSprint();

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/find_sprint_tasks").accept(MediaType.APPLICATION_JSON)
                .query("spId", spId)
                .query("offset", 1)
                .query("limit", 1);

        var resp = client.get();
        assertEquals(204, resp.getStatus());
    }

    @Test
    public void findTaskForSprintNotFoundTest() {
        createProject();
        createUser();
        var ctrId = createContributor();
        createTask(ctrId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/find_sprint_tasks").accept(MediaType.APPLICATION_JSON)
                .query("spId", 10)
                .query("offset", 0)
                .query("limit", 2);

        var resp = client.get();
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.SPRINT_NOT_EXISTS, msg);
    }

    @Test
    public void countSprintTasksTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/count_sprint_tasks")
                .accept(MediaType.APPLICATION_JSON)
                .query("sprintId", 7);

        var res = client.get();
        assertEquals(200, res.getStatus());

        var count = res.readEntity(Integer.class);
        assertEquals(Integer.valueOf(7), count);

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

    private long createSprint() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/create_sprint").accept(MediaType.APPLICATION_JSON);

        var resp = client.get();

        return resp.readEntity(Sprint.class).getId();
    }

    public void updateTaskState(long taskId, TaskState state) {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/task/update_state").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("id", String.valueOf(taskId));
        form.param("state", state.name());

        client.post(form);

        client.close();
    }
}
