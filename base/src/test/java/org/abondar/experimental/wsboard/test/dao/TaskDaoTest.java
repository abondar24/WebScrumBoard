package org.abondar.experimental.wsboard.test.dao;

import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.dao.ContributorDao;
import org.abondar.experimental.wsboard.dao.ProjectDao;
import org.abondar.experimental.wsboard.dao.SprintDao;
import org.abondar.experimental.wsboard.dao.TaskDao;
import org.abondar.experimental.wsboard.dao.UserDao;
import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.User;
import org.abondar.experimental.wsboard.datamodel.UserRole;
import org.abondar.experimental.wsboard.datamodel.task.TaskState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class TaskDaoTest {

    private static Logger logger = LoggerFactory.getLogger(TaskDaoTest.class);

    @Autowired
    private DataMapper mapper;

    @Autowired
    @Qualifier("taskDao")
    private TaskDao dao;

    @Autowired
    @Qualifier("userDao")
    private UserDao userDao;

    @Autowired
    @Qualifier("projectDao")
    private ProjectDao projectDao;

    @Autowired
    @Qualifier("contributorDao")
    private ContributorDao contributorDao;

    @Autowired
    @Qualifier("sprintDao")
    private SprintDao sprintDao;


    @Test
    public void createTaskTest() throws Exception {
        logger.info("Create task test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true);

        assertTrue(task.getId()>0);

        cleanData();
    }


    @Test
    public void createTaskNoContributorTest() {
        logger.info("Create task no contributor test");

        assertThrows(DataExistenceException.class, () ->
                dao.createTask(100, new Date(), false));

    }


    @Test
    public void createTaskInactiveContributorTest() throws Exception {
        logger.info("Create task test");


        var usr = createUser("");
        var prj = createProject(true);

        var ownr = createUser("owner");
        contributorDao.createContributor(ownr.getId(), prj.getId(), true);

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        userDao.deleteUser(contr.getUserId());

        contributorDao.updateContributor(contr.getId(), null, false);

        assertThrows(DataExistenceException.class, () ->
                dao.createTask(contr.getId(), new Date(), true));
        cleanData();
    }

    @Test
    public void createTaskNullDateTest() throws Exception {
        logger.info("Create task null date test");


        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        assertThrows(DataCreationException.class, () ->
                dao.createTask(contr.getId(), null, false));


        cleanData();
    }


    @Test
    public void updateTaskContributorTest() throws Exception {
        logger.info("Update task contributor test");

        var usr = createUser("");
        var prj = createProject(true);

        var usr1 = userDao.createUser("login1", "pwd",
                "email@email.com", "fname", "lname",
                List.of(UserRole.Developer.name(), UserRole.DevOps.name()));

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var contr1 = contributorDao.createContributor(usr1.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true);
        var id = task.getId();
        var res = dao.updateTask(task.getId(), contr1.getId(), true,
                null);

        assertEquals(id, task.getId());
        assertEquals(contr1.getId(), res.getContributorId());

        cleanData();
    }


    @Test
    public void updateTaskNotExistsTest() {
        logger.info("Update task not exists test");

        assertThrows(DataExistenceException.class, () ->
                dao.updateTask(100, 100L, null, null));
    }

    @Test
    public void updateTaskContributorNotExistsTest() throws Exception {
        logger.info("Update task contributor not exists test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false);

        assertThrows(DataExistenceException.class, () ->
                dao.updateTask(task.getId(), 100L, null, null));


        cleanData();
    }


    @Test
    public void updateTaskStoryPointsTest() throws Exception {
        logger.info("Update task story points test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), true);

        int storyPoints = 2;
        var res = dao.updateTask(task.getId(), null,
                null, storyPoints);

        assertEquals(storyPoints, res.getStoryPoints());

        cleanData();
    }


    @Test
    public void updateTaskSprintTest() throws Exception {
        logger.info("Update task sprint test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), true);
        var sprint = sprintDao.createSprint("test", new Date(), new Date());

        var res = dao.updateTaskSprint(task.getId(), sprint.getId());

        assertEquals(sprint.getId(), res.getSprintId());

        cleanData();
    }


    @Test
    public void updateTaskStateTest() throws Exception {
        logger.info("Update task state test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false);

        var res = dao.updateTaskState(task.getId(), TaskState.InDevelopment.name());

        assertEquals(TaskState.InDevelopment, res.getTaskState());
        assertEquals(TaskState.Created, res.getPrevState());

        cleanData();
    }


    @Test
    public void updateTaskStateUnknownTest() throws Exception {
        logger.info("Update task state unknown test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false);

        assertThrows(DataExistenceException.class, () ->
                dao.updateTaskState(task.getId(), "test"));

        cleanData();
    }

    @Test
    public void updateTaskStateNotExistsTest() {
        logger.info("Update task state task not exists test");

        assertThrows(DataExistenceException.class, () ->
                dao.updateTaskState(100, TaskState.InDeployment.name()));

    }

    @Test
    public void updateTaskStateAlreadyCompletedTest() throws Exception {
        logger.info("Update task state already completed test");

        var usr = createUser("");
        usr = userDao.updateUser(usr.getId(),null,null,null,
                List.of(UserRole.Developer.name(), UserRole.DevOps.name(),UserRole.QA.name()),null);

        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false);
        dao.updateTaskState(task.getId(), TaskState.InTest.name());
        dao.updateTaskState(task.getId(), TaskState.Completed.name());

        assertThrows(DataCreationException.class, () ->
                dao.updateTaskState(task.getId(), TaskState.InDevelopment.name()));

        cleanData();
    }

    @Test
    public void updateTaskStateAlreadyCreatedTest() throws Exception {
        logger.info("Update task state already created test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false);

        assertThrows(DataCreationException.class, () ->
                dao.updateTaskState(task.getId(), TaskState.Created.name()));


        cleanData();
    }

    @Test
    public void updateTaskStatePausedTest() throws Exception {
        logger.info("Update task state paused test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false);
        dao.updateTaskState(task.getId(), TaskState.Paused.name());

        assertThrows(DataCreationException.class, () ->
                dao.updateTaskState(task.getId(), TaskState.InDevelopment.name()));

        cleanData();
    }


    @Test
    public void updateTaskStateNoDevOpsTest() throws Exception {
        logger.info("Update task state no dev ops test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false);

        assertThrows(DataCreationException.class, () ->
                dao.updateTaskState(task.getId(), TaskState.InDeployment.name()));

        cleanData();
    }

    @Test
    public void updateTaskStateNoMovesTest() throws Exception {
        logger.info("Update task state no moves test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false);

        assertThrows(DataCreationException.class, () ->
                dao.updateTaskState(task.getId(), TaskState.InCodeReview.name()));

        cleanData();
    }

    @Test
    public void updateTaskStateRoleUpdateNeededTest() throws Exception {
        logger.info("Update task state role update needed test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false);
        dao.updateTaskState(task.getId(), TaskState.InDevelopment.name());
        dao.updateTaskState(task.getId(), TaskState.InCodeReview.name());

        assertThrows(DataCreationException.class, () ->
                 dao.updateTaskState(task.getId(), TaskState.InTest.name()));

        cleanData();
    }

    @Test
    public void updateTaskStateCompletedEndDateTest() throws Exception {
        logger.info("Update task state completed end date test");

        var usr = createUser("");
        usr = userDao.updateUser(usr.getId(),null,null,null,
                List.of(UserRole.Developer.name(), UserRole.DevOps.name(),UserRole.QA.name()),null);

        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false);
        dao.updateTaskState(task.getId(), TaskState.InDevelopment.name());
        dao.updateTaskState(task.getId(), TaskState.InCodeReview.name());
        dao.updateTaskState(task.getId(), TaskState.InTest.name());

        var res = dao.updateTaskState(task.getId(), TaskState.Completed.name());


        assertEquals(TaskState.Completed, res.getTaskState());
        assertNotNull(res.getEndDate());

        cleanData();
    }

    @Test
    public void deleteTaskTest() throws Exception {
        logger.info("Delete task test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true);
        var res = dao.deleteTask(task.getId());

        Assertions.assertTrue(res);

        cleanData();
    }


    @Test
    public void deleteTaskNoExistsTest() {
        logger.info("Delete task not exists test");

        var res = dao.deleteTask(100);

        Assertions.assertFalse(res);
    }

    @Test
    public void deleteTaskSprintTest() throws Exception {
        logger.info("Delete task sprint test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true);
        var sp = sprintDao.createSprint("test", new Date(), new Date());
        dao.updateTaskSprint(task.getId(), sp.getId());


        var res = dao.deleteTask(task.getId());

        Assertions.assertTrue(res);

        cleanData();
    }


    @Test
    public void getTaskByIdTest() throws Exception {
        logger.info("Delete task test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true);
        var res = dao.getTaskById(task.getId());

        assertEquals(task.getId(), res.getId());

        cleanData();
    }

    @Test
    public void getTasksForProjectTest() throws Exception {
        logger.info("Get tasks for project test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true);
        var res = dao.getTasksForProject(prj.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());

        cleanData();
    }

    @Test
    public void getTasksForContributorTest() throws Exception {
        logger.info("Get tasks for contributor test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true);
        var res = dao.getTasksForContributor(contr.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());

        cleanData();
    }

    @Test
    public void getTasksForUserTest() throws Exception {
        logger.info("Get tasks for user test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true);
        var res = dao.getTasksForUser(usr.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());

        cleanData();
    }

    @Test
    public void getTasksForSprintTest() throws Exception {
        logger.info("Get tasks for sprint test");

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true);
        var sprint = sprintDao.createSprint("test", new Date(), new Date());

        task = dao.updateTaskSprint(task.getId(), sprint.getId());

        var res = dao.getTasksForSprint(sprint.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());

        cleanData();
    }


    private User createUser(String login) throws Exception {
        if (login.isBlank()){
            login = "login";
        }
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        return userDao.createUser(login, password, email, firstName, lastName, roles);
    }

    private Project createProject(boolean isActive) throws Exception {
        var name = "test";
        var startDate = new Date();

        var prj = projectDao.createProject(name, startDate);
        if (isActive) {
            prj = projectDao.updateProject(prj.getId(), null, null, true, null);
        }

        return prj;
    }

    private void cleanData() {
        mapper.deleteTasks();
        mapper.deleteSprints();
        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();
    }
}
