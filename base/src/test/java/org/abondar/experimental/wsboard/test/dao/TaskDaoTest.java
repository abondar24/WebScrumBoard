package org.abondar.experimental.wsboard.test.dao;

import org.abondar.experimental.wsboard.dao.SprintDao;
import org.abondar.experimental.wsboard.dao.TaskDao;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.task.TaskState;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TaskDaoTest  extends BaseDaoTest{


    @Autowired
    @Qualifier("taskDao")
    private TaskDao dao;

    @Autowired
    @Qualifier("sprintDao")
    private SprintDao sprintDao;


    @Test
    public void createTaskTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");

        assertTrue(task.getId()>0);

    }


    @Test
    public void createTaskNoContributorTest() {
        cleanData();
        assertThrows(DataExistenceException.class, () ->
                dao.createTask(100, new Date(), false, "name", "descr"));

    }


    @Test
    public void createTaskInactiveContributorTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);

        var ownr = createUser("owner");
        contributorDao.createContributor(ownr.getId(), prj.getId(), true);

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        userDao.deleteUser(contr.getUserId());

        contributorDao.updateContributor(usr.getId(),prj.getId(), null, false);

        assertThrows(DataExistenceException.class, () ->
                dao.createTask(contr.getId(), new Date(), true, "name", "descr"));

    }



    @Test
    public void updateTaskContributorTest() throws Exception {
        cleanData();

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

    }


    @Test
    public void updateTaskNotExistsTest() {
        cleanData();

        assertThrows(DataExistenceException.class, () ->
                dao.updateTask(100, 100L, null, null, null, null));
    }

    @Test
    public void updateTaskContributorNotExistsTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, null, null);

        assertThrows(DataExistenceException.class, () ->
                dao.updateTask(task.getId(), 100L, null, null, null, null));

    }


    @Test
    public void updateTaskStoryPointsTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");

        int storyPoints = 2;
        var res = dao.updateTask(task.getId(), null,
                null, storyPoints, null, null);

        assertEquals(storyPoints, res.getStoryPoints());

    }

    @Test
    public void updateTaskNameTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");

        int storyPoints = 2;
        var res = dao.updateTask(task.getId(), null,
                null, storyPoints, "new name", null);

        assertEquals(storyPoints, res.getStoryPoints());

    }

    @Test
    public void updateTaskDescriptionTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");

        int storyPoints = 2;
        var res = dao.updateTask(task.getId(), null,
                null, storyPoints, null, "newDescr");

        assertEquals(storyPoints, res.getStoryPoints());

    }


    @Test
    public void updateTaskSprintTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");
        var sprint = sprintDao.createSprint("test", new Date(), new Date(),prj.getId());

        var res = dao.updateTaskSprint(task.getId(), sprint.getId());

        assertEquals(sprint.getId(), res.getSprintId());

    }


    @Test
    public void updateTaskStateTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, "name", "descr");

        var res = dao.updateTaskState(task.getId(), TaskState.IN_DEVELOPMENT.name());

        assertEquals(TaskState.IN_DEVELOPMENT, res.getTaskState());
        assertEquals(TaskState.CREATED, res.getPrevState());

    }


    @Test
    public void updateTaskStateUnknownTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, "name", "descr");

        assertThrows(DataExistenceException.class, () ->
                dao.updateTaskState(task.getId(), "test"));

    }

    @Test
    public void updateTaskStateNotExistsTest() {
        cleanData();

        assertThrows(DataExistenceException.class, () ->
                dao.updateTaskState(100, TaskState.IN_DEPLOYMENT.name()));

    }

    @Test
    public void updateTaskStateAlreadyCompletedTest() throws Exception {
        cleanData();

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

    }

    @Test
    public void updateTaskStateAlreadyCreatedTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, "name", "descr");

        assertThrows(DataCreationException.class, () ->
                dao.updateTaskState(task.getId(), TaskState.CREATED.name()));

    }

    @Test
    public void updateTaskStatePausedTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, "name", "descr");
        dao.updateTaskState(task.getId(), TaskState.PAUSED.name());

        assertThrows(DataCreationException.class, () ->
                dao.updateTaskState(task.getId(), TaskState.IN_DEVELOPMENT.name()));

    }


    @Test
    public void updateTaskStateNoDevOpsTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, "name", "descr");

        assertThrows(DataCreationException.class, () ->
                dao.updateTaskState(task.getId(), TaskState.IN_DEPLOYMENT.name()));

    }

    @Test
    public void updateTaskStateNoMovesTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, "name", "descr");

        assertThrows(DataCreationException.class, () ->
                dao.updateTaskState(task.getId(), TaskState.IN_CODE_REVIEW.name()));

    }

    @Test
    public void updateTaskStateRoleUpdateNeededTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var task = dao.createTask(contr.getId(), new Date(), false, "name", "descr");
        dao.updateTaskState(task.getId(), TaskState.IN_DEVELOPMENT.name());
        dao.updateTaskState(task.getId(), TaskState.IN_CODE_REVIEW.name());

        assertThrows(DataCreationException.class, () ->
                dao.updateTaskState(task.getId(), TaskState.IN_TEST.name()));

    }

    @Test
    public void updateTaskStateCompletedEndDateTest() throws Exception {
        cleanData();

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

    }

    @Test
    public void deleteTaskTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");
        var res = dao.deleteTask(task.getId());

        Assertions.assertTrue(res);

    }


    @Test
    public void deleteTaskNoExistsTest() {
        cleanData();

        var res = dao.deleteTask(100);

        Assertions.assertFalse(res);
    }

    @Test
    public void deleteTaskSprintTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");
        var sp = sprintDao.createSprint("test", new Date(), new Date(),prj.getId());
        dao.updateTaskSprint(task.getId(), sp.getId());


        var res = dao.deleteTask(task.getId());

        Assertions.assertTrue(res);

    }


    @Test
    public void getTaskByIdTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");
        var res = dao.getTaskById(task.getId());

        assertEquals(task.getId(), res.getId());

    }

    @Test
    public void getTasksForProjectTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");
        var res = dao.getTasksForProject(prj.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());

    }

    @Test
    public void getTasksForContributorTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");
        var res = dao.getTasksForContributor(contr.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());


    }

    @Test
    public void countContributorTasks() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        dao.createTask(contr.getId(), new Date(), true, "name", "descr");

        var res = dao.countContributorTasks(contr.getId());

        assertEquals(Integer.valueOf(1), res);

    }

    @Test
    public void countContributorNotFoundTasks() {
        cleanData();

        assertThrows(DataExistenceException.class,()-> dao.countContributorTasks(100L));
   }


    @Test
    public void getTasksForUserTest() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = dao.createTask(contr.getId(), new Date(), true, "name", "descr");
        var res = dao.getTasksForUser(usr.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());

    }

    @Test
    public void countUserTasks() throws Exception {
        cleanData();

        var usr = createUser("");
        var prj = createProject(true);
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        dao.createTask(contr.getId(), new Date(), true, "name", "descr");

        var res = dao.countUserTasks(usr.getId());

        assertEquals(Integer.valueOf(1), res);

    }

    @Test
    public void countUserNotFoundTasks() {
        cleanData();


        assertThrows(DataExistenceException.class,()-> dao.countUserTasks(100L));
    }

    @Test
    public void getTasksForSprintTest() throws Exception {
        cleanData();

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


}
