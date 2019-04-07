package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.datamodel.UserRole;
import org.abondar.experimental.wsboard.datamodel.task.TaskState;
import org.junit.jupiter.api.Disabled;
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

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void createTaskTest() throws Exception {
        logger.info("Create task test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);

        var task = dao.createTask(contr.getObject().getId(), new Date(), true);

        assertNull(task.getMessage());

        cleanData();
    }


    @Test
    public void createTaskNoContributorTest() {
        logger.info("Create task no contributor test");

        var task = dao.createTask(100, new Date(), false);

        assertEquals(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS, task.getMessage());

    }


    //TODO: check deletion with contributor test as well
    @Test
    @Disabled
    public void createTaskInactiveContributorTest() throws Exception {
        logger.info("Create task test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        userDao.deleteUser(contr.getObject().getUserId());

        contributorDao.updateContributorAsOwner(contr.getObject().getId(), null, false);
        var task = dao.createTask(contr.getObject().getId(), new Date(), true);

        assertEquals(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS, task.getMessage());
        cleanData();
    }

    @Test
    public void createTaskNullDateTest() throws Exception {
        logger.info("Create task null date test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);

        var task = dao.createTask(contr.getObject().getId(), null, false);

        assertEquals(ErrorMessageUtil.TASK_START_DATE_NOT_SET, task.getMessage());


        cleanData();
    }


    @Test
    public void updateTaskContributorTest() throws Exception {
        logger.info("Update task contributor test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);
        var usr1 = userDao.createUser("login1", password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        var contr1 = contributorDao.createContributor(usr1.getObject().getId(), prj.getObject().getId(), false);

        var task = dao.createTask(contr.getObject().getId(), new Date(), true);

        var res = dao.updateTask(task.getObject().getId(), contr1.getObject().getId(), true);


        assertNull(res.getMessage());
        assertEquals(contr1.getObject().getId(), res.getObject().getContributorId());

        cleanData();
    }


    @Test
    public void updateTaskNotExistsTest() {
        logger.info("Update task not exists test");

        var res = dao.updateTask(100, 100, null);

        assertEquals(ErrorMessageUtil.TASK_NOT_EXISTS, res.getMessage());
    }

    @Test
    public void updateTaskContributorNotExistsTest() throws Exception {
        logger.info("Update task contributor not exists test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        var task = dao.createTask(contr.getObject().getId(), new Date(), false);

        var res = dao.updateTask(task.getObject().getId(), 100, null);

        assertEquals(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS, res.getMessage());

        cleanData();
    }


    @Test
    public void updateTaskStoryPointsTest() throws Exception {
        logger.info("Update task story points test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        var task = dao.createTask(contr.getObject().getId(), new Date(), true);

        int storyPoints = 2;
        var res = dao.updateTaskStoryPoints(task.getObject().getId(), storyPoints);

        assertNull(res.getMessage());
        assertEquals(storyPoints, res.getObject().getStoryPoints());

        cleanData();
    }


    @Test
    public void updateTaskStoryPointsNullTest() throws Exception {
        logger.info("Update task story points null test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        var task = dao.createTask(contr.getObject().getId(), new Date(), false);

        var res = dao.updateTaskStoryPoints(task.getObject().getId(), null);

        assertEquals(ErrorMessageUtil.TASK_STORY_POINTS_NOT_SET, res.getMessage());

        cleanData();
    }


    @Test
    public void updateTaskStateTest() throws Exception {
        logger.info("Update task state test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        var task = dao.createTask(contr.getObject().getId(), new Date(), false);

        var res = dao.updateTaskState(task.getObject().getId(), TaskState.InDevelopment.name());

        assertNull(res.getMessage());
        assertEquals(TaskState.InDevelopment, res.getObject().getTaskState());
        assertEquals(TaskState.Created, res.getObject().getPrevState());

        cleanData();
    }


    @Test
    public void updateTaskStateUnknownTest() throws Exception {
        logger.info("Update task state unknown test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        var task = dao.createTask(contr.getObject().getId(), new Date(), false);

        var res = dao.updateTaskState(task.getObject().getId(), "test");

        assertEquals(ErrorMessageUtil.TASK_STATE_UNKNOWN, res.getMessage());

        cleanData();
    }

    @Test
    public void updateTaskStateNotExistsTest() throws Exception {
        logger.info("Update task state task not exists test");

        var res = dao.updateTaskState(100, TaskState.InDeployment.name());

        assertEquals(ErrorMessageUtil.TASK_NOT_EXISTS, res.getMessage());

    }

    @Test
    public void updateTaskStateAlreadyCompletedTest() throws Exception {
        logger.info("Update task state already completed test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        var task = dao.createTask(contr.getObject().getId(), new Date(), false);
        dao.updateTaskState(task.getObject().getId(), TaskState.InTest.name());
        dao.updateTaskState(task.getObject().getId(), TaskState.Completed.name());

        var res = dao.updateTaskState(task.getObject().getId(), TaskState.InDevelopment.name());

        assertEquals(ErrorMessageUtil.TASK_ALREADY_COMPLETED, res.getMessage());

        cleanData();
    }

    @Test
    public void updateTaskStateAlreadyCreatedTest() throws Exception {
        logger.info("Update task state already created test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        var task = dao.createTask(contr.getObject().getId(), new Date(), false);

        var res = dao.updateTaskState(task.getObject().getId(), TaskState.Created.name());

        assertEquals(ErrorMessageUtil.TASK_ALREADY_CREATED, res.getMessage());

        cleanData();
    }

    @Test
    public void updateTaskStatePausedTest() throws Exception {
        logger.info("Update task state paused test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        var task = dao.createTask(contr.getObject().getId(), new Date(), false);
        dao.updateTaskState(task.getObject().getId(), TaskState.Paused.name());

        var res = dao.updateTaskState(task.getObject().getId(), TaskState.InDevelopment.name());

        assertEquals(ErrorMessageUtil.TASK_WRONG_STATE_AFTER_PAUSE, res.getMessage());

        cleanData();
    }


    @Test
    public void updateTaskStateNoDevOpsTest() throws Exception {
        logger.info("Update task state no dev ops test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        var task = dao.createTask(contr.getObject().getId(), new Date(), false);

        var res = dao.updateTaskState(task.getObject().getId(), TaskState.InDeployment.name());

        assertEquals(ErrorMessageUtil.TASK_DEV_OPS_NOT_ENABLED, res.getMessage());

        cleanData();
    }

    @Test
    public void updateTaskStateNoMovesTest() throws Exception {
        logger.info("Update task state no moves test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        var task = dao.createTask(contr.getObject().getId(), new Date(), false);

        var res = dao.updateTaskState(task.getObject().getId(), TaskState.InCodeReview.name());

        assertEquals(ErrorMessageUtil.TASK_MOVE_NOT_AVAILABLE, res.getMessage());

        cleanData();
    }

    @Test
    public void updateTaskStateRoleUpdateNeededTest() throws Exception {
        logger.info("Update task state role update needed test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        var task = dao.createTask(contr.getObject().getId(), new Date(), false);
        dao.updateTaskState(task.getObject().getId(), TaskState.InDevelopment.name());
        dao.updateTaskState(task.getObject().getId(), TaskState.InCodeReview.name());

        var res = dao.updateTaskState(task.getObject().getId(), TaskState.InTest.name());

        assertEquals(ErrorMessageUtil.TASK_CONTRIBUTOR_UPDATE, res.getMessage());
        assertNotNull(res.getObject());
        assertEquals(TaskState.InTest, res.getObject().getTaskState());
        assertEquals(TaskState.InCodeReview, res.getObject().getPrevState());

        cleanData();
    }

    @Test
    public void updateTaskStateCompletedEndDateTest() throws Exception {
        logger.info("Update task state completed end date test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        var task = dao.createTask(contr.getObject().getId(), new Date(), false);
        dao.updateTaskState(task.getObject().getId(), TaskState.InDevelopment.name());
        dao.updateTaskState(task.getObject().getId(), TaskState.InCodeReview.name());
        dao.updateTaskState(task.getObject().getId(), TaskState.InTest.name());

        var res = dao.updateTaskState(task.getObject().getId(), TaskState.Completed.name());


        assertEquals(TaskState.Completed, res.getObject().getTaskState());
        assertNotNull(res.getObject().getEndDate());

        cleanData();
    }

    @Test
    public void deleteTaskTest() throws Exception {
        logger.info("Delete task test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);

        var task = dao.createTask(contr.getObject().getId(), new Date(), true);
        var res = dao.deleteTask(task.getObject().getId());

        assertTrue(res);

        cleanData();
    }


    @Test
    public void deleteTaskNoExistsTest() {
        logger.info("Delete task not exists test");

        var res = dao.deleteTask(100);

        assertFalse(res);
    }

    //TODO: delete task with sprint test


    @Test
    public void getTaskByTest() throws Exception {
        logger.info("Delete task test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);

        var task = dao.createTask(contr.getObject().getId(), new Date(), true);
        var res = dao.getTaskById(task.getObject().getId());

        assertEquals(task.getObject().getId(), res.getObject().getId());

        cleanData();
    }

    private void cleanData() {
        mapper.deleteTasks();
        mapper.deleteSprints();
        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();
    }
}
