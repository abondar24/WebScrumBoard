package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.datamodel.UserRole;
import org.abondar.experimental.wsboard.datamodel.task.TaskType;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
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

        var task = dao.createTask(contr.getObject().getId(), TaskType.Development.name(), new Date(), true);

        assertNull(task.getMessage());

        cleanData();
    }


    @Test
    public void createTaskUnknownTypeTest() throws Exception {
        logger.info("Create task unknown type test");

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

        var task = dao.createTask(contr.getObject().getId(), "test", new Date(), false);

        assertEquals(ErrorMessageUtil.TASK_TYPE_UNKNOWN, task.getMessage());
        cleanData();
    }

    @Test
    public void createTaskTypeMismatchTest() throws Exception {
        logger.info("Create task  type mismatch test");

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

        var task = dao.createTask(contr.getObject().getId(), TaskType.Testing.name(), new Date(), true);

        assertEquals(ErrorMessageUtil.TASK_TYPE_MISMATCH, task.getMessage());
        cleanData();
    }


    @Test
    public void createTaskNoContributorTest() {
        logger.info("Create task no contributor test");

        var task = dao.createTask(100, TaskType.Development.name(), new Date(), false);

        assertEquals(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS, task.getMessage());

    }


    @Test
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

        var task = dao.createTask(contr.getObject().getId(), TaskType.Testing.name(), new Date(), true);

        assertEquals(ErrorMessageUtil.TASK_TYPE_MISMATCH, task.getMessage());
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

        var task = dao.createTask(contr.getObject().getId(), TaskType.Development.name(), null, false);

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

        var task = dao.createTask(contr.getObject().getId(), TaskType.Development.name(), new Date(), true);

        var res = dao.updateTask(task.getObject().getId(), contr1.getObject().getId(), TaskType.Development.name());


        assertNull(res.getMessage());
        assertEquals(contr1.getObject().getId(), res.getObject().getContributorId());

        cleanData();
    }


    @Test
    public void updateTaskNotExistsTest() {
        logger.info("Update task not exists test");

        var res = dao.updateTask(100, 100, TaskType.DevOps.name());

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
        var task = dao.createTask(contr.getObject().getId(), TaskType.DevOps.name(), new Date(), false);

        var res = dao.updateTask(task.getObject().getId(), 100, TaskType.DevOps.name());

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
        var task = dao.createTask(contr.getObject().getId(), TaskType.DevOps.name(), new Date(), true);

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
        var task = dao.createTask(contr.getObject().getId(), TaskType.DevOps.name(), new Date(), false);

        var res = dao.updateTaskStoryPoints(task.getObject().getId(), null);

        assertEquals(ErrorMessageUtil.TASK_STORY_POINTS_NOT_SET, res.getMessage());

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

        var task = dao.createTask(contr.getObject().getId(), TaskType.DevOps.name(), new Date(), true);
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

    private void cleanData() {
        mapper.deleteTasks();
        mapper.deleteSprints();
        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();
    }
}
