package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.datamodel.UserRole;
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

        var task = dao.createTask(contr.getObject().getId(), new Date());

        assertNull(task.getMessage());


        cleanData();
    }


    @Test
    public void createTaskNoContributorTest() {
        logger.info("Create task no contributor test");

        var task = dao.createTask(100, new Date());

        assertEquals(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS, task.getMessage());

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

        var task = dao.createTask(contr.getObject().getId(), null);

        assertEquals(ErrorMessageUtil.TASK_START_DATE_NOT_SET, task.getMessage());


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

        var task = dao.createTask(contr.getObject().getId(), new Date());
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
