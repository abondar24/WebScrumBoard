package org.abondar.experimental.wsboard.test.dao;

import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.dao.ContributorDao;
import org.abondar.experimental.wsboard.dao.ProjectDao;
import org.abondar.experimental.wsboard.dao.SprintDao;
import org.abondar.experimental.wsboard.dao.TaskDao;
import org.abondar.experimental.wsboard.dao.UserDao;
import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.task.TaskState;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = WebScrumBoardApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class TaskDaoTest {


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
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");

        assertTrue(task.getId()>0);

        cleanData();
    }


    @Test
    public void createTaskNoContributorTest() {
        assertThrows(DataExistenceException.class, () ->
                dao.createTask(100, new Date(), false, "name", "descr"));

    }


    @Test
    public void createTaskInactiveContributorTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);

        var ownr = createUser("owner");
        contributorDao.createContributor(ownr.getId(), prj.getId(), true);

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        userDao.deleteUser(contr.getUserId());

        contributorDao.updateContributor(usr.getId(),prj.getId(), null, false);

        assertThrows(DataExistenceException.class, () ->
                dao.createTask(contr.getId(), new Date(), true, "name", "descr"));
        cleanData();
    }



    @Test
    public void updateTaskContributorTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);

        var usr1 = userDao.createUser("login1", "pwd",
                "email@email.com", "fname", "lname",
                UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name());

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var contr1 = contributorDao.createContributor(usr1.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");
        var id = task.getId();
        var res = dao.updateTask(task.getId(), contr1.getId(), true,
                null, null, null);

        assertEquals(id, task.getId());
        assertEquals(contr1.getId(), res.getContributorId());

        cleanData();
    }


    @Test
    public void updateTaskNotExistsTest() {
        assertThrows(DataExistenceException.class, () ->
                dao.updateTask(100, 100L, null, null, null, null));
    }

    @Test
    public void updateTaskContributorNotExistsTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, null, null);

        assertThrows(DataExistenceException.class, () ->
                dao.updateTask(task.getId(), 100L, null, null, null, null));


        cleanData();
    }


    @Test
    public void updateTaskStoryPointsTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");

        int storyPoints = 2;
        var res = dao.updateTask(task.getId(), null,
                null, storyPoints, null, null);

        assertEquals(storyPoints, res.getStoryPoints());

        cleanData();
    }

    @Test
    public void updateTaskNameTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");

        int storyPoints = 2;
        var res = dao.updateTask(task.getId(), null,
                null, storyPoints, "new name", null);

        assertEquals(storyPoints, res.getStoryPoints());

        cleanData();
    }

    @Test
    public void updateTaskDescriptionTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");

        int storyPoints = 2;
        var res = dao.updateTask(task.getId(), null,
                null, storyPoints, null, "newDescr");

        assertEquals(storyPoints, res.getStoryPoints());

        cleanData();
    }


    @Test
    public void updateTaskSprintTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");
        var sprint = sprintDao.createSprint("test", new Date(), new Date(),prj.getId());

        var res = dao.updateTaskSprint(task.getId(), sprint.getId());

        assertEquals(sprint.getId(), res.getSprintId());

        cleanData();
    }


    @Test
    public void updateTaskStateTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, "name", "descr");

        var res = dao.updateTaskState(task.getId(), TaskState.IN_DEVELOPMENT.name());

        assertEquals(TaskState.IN_DEVELOPMENT, res.getTaskState());
        assertEquals(TaskState.CREATED, res.getPrevState());

        cleanData();
    }


    @Test
    public void updateTaskStateUnknownTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, "name", "descr");

        assertThrows(DataExistenceException.class, () ->
                dao.updateTaskState(task.getId(), "test"));

        cleanData();
    }

    @Test
    public void updateTaskStateNotExistsTest() {
        assertThrows(DataExistenceException.class, () ->
                dao.updateTaskState(100, TaskState.IN_DEPLOYMENT.name()));

    }

    @Test
    public void updateTaskStateAlreadyCompletedTest() throws Exception {
        var usr = createUser("");
        usr = userDao.updateUser(usr.getId(),null,null,null,
                UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name() + ";" + UserRole.QA.name(), null);

        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, "name", "descr");
        dao.updateTaskState(task.getId(), TaskState.IN_TEST.name());
        dao.updateTaskState(task.getId(), TaskState.COMPLETED.name());

        assertThrows(DataCreationException.class, () ->
                dao.updateTaskState(task.getId(), TaskState.IN_DEVELOPMENT.name()));

        cleanData();
    }

    @Test
    public void updateTaskStateAlreadyCreatedTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, "name", "descr");

        assertThrows(DataCreationException.class, () ->
                dao.updateTaskState(task.getId(), TaskState.CREATED.name()));


        cleanData();
    }

    @Test
    public void updateTaskStatePausedTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, "name", "descr");
        dao.updateTaskState(task.getId(), TaskState.PAUSED.name());

        assertThrows(DataCreationException.class, () ->
                dao.updateTaskState(task.getId(), TaskState.IN_DEVELOPMENT.name()));

        cleanData();
    }


    @Test
    public void updateTaskStateNoDevOpsTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, "name", "descr");

        assertThrows(DataCreationException.class, () ->
                dao.updateTaskState(task.getId(), TaskState.IN_DEPLOYMENT.name()));

        cleanData();
    }

    @Test
    public void updateTaskStateNoMovesTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, "name", "descr");

        assertThrows(DataCreationException.class, () ->
                dao.updateTaskState(task.getId(), TaskState.IN_CODE_REVIEW.name()));

        cleanData();
    }

    @Test
    public void updateTaskStateRoleUpdateNeededTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, "name", "descr");
        dao.updateTaskState(task.getId(), TaskState.IN_DEVELOPMENT.name());
        dao.updateTaskState(task.getId(), TaskState.IN_CODE_REVIEW.name());

        assertThrows(DataCreationException.class, () ->
                dao.updateTaskState(task.getId(), TaskState.IN_TEST.name()));

        cleanData();
    }

    @Test
    public void updateTaskStateCompletedEndDateTest() throws Exception {
        var usr = createUser("");
        usr = userDao.updateUser(usr.getId(),null,null,null,
                UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name() + ";" + UserRole.QA.name(), null);

        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, "name", "descr");
        dao.updateTaskState(task.getId(), TaskState.IN_DEVELOPMENT.name());
        dao.updateTaskState(task.getId(), TaskState.IN_CODE_REVIEW.name());
        dao.updateTaskState(task.getId(), TaskState.IN_TEST.name());

        var res = dao.updateTaskState(task.getId(), TaskState.COMPLETED.name());


        assertEquals(TaskState.COMPLETED, res.getTaskState());
        assertNotNull(res.getEndDate());

        cleanData();
    }

    @Test
    public void deleteTaskTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");
        var res = dao.deleteTask(task.getId());

        Assertions.assertTrue(res);

        cleanData();
    }


    @Test
    public void deleteTaskNoExistsTest() {
        var res = dao.deleteTask(100);

        Assertions.assertFalse(res);
    }

    @Test
    public void deleteTaskSprintTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");
        var sp = sprintDao.createSprint("test", new Date(), new Date(),prj.getId());
        dao.updateTaskSprint(task.getId(), sp.getId());


        var res = dao.deleteTask(task.getId());

        Assertions.assertTrue(res);

        cleanData();
    }


    @Test
    public void getTaskByIdTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");
        var res = dao.getTaskById(task.getId());

        assertEquals(task.getId(), res.getId());

        cleanData();
    }

    @Test
    public void getTasksForProjectTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");
        var res = dao.getTasksForProject(prj.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());

        cleanData();
    }

    @Test
    public void getTasksForContributorTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");
        var res = dao.getTasksForContributor(contr.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());

        cleanData();
    }

    @Test
    public void countContributorTasks() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");

        var res = dao.countContributorTasks(contr.getId());

        assertEquals(Integer.valueOf(1), res);

        cleanData();
    }

    @Test
    public void countContributorNotFoundTasks() {

        assertThrows(DataExistenceException.class,()-> dao.countContributorTasks(100L));
   }


    @Test
    public void getTasksForUserTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");
        var res = dao.getTasksForUser(usr.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());

        cleanData();
    }

    @Test
    public void countUserTasks() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");

        var res = dao.countUserTasks(usr.getId());

        assertEquals(Integer.valueOf(1), res);

        cleanData();
    }

    @Test
    public void countUserNotFoundTasks() {

        assertThrows(DataExistenceException.class,()-> dao.countUserTasks(100L));
    }

    @Test
    public void getTasksForSprintTest() throws Exception {
        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");
        var sprint = sprintDao.createSprint("test", new Date(), new Date(),
               prj.getId());

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
        var roles = UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name();

        return userDao.createUser(login, password, email, firstName, lastName, roles);
    }

    private Project createProject(boolean isActive) throws Exception {
        var name = "test";
        var startDate = new Date();

        var prj = projectDao.createProject(name, startDate);
        if (isActive) {
            prj = projectDao.updateProject(prj.getId(), null, null, true, null,null);
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
