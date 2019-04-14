package org.abondar.experimental.wsboard.base.data;

import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.Sprint;
import org.abondar.experimental.wsboard.datamodel.User;
import org.abondar.experimental.wsboard.datamodel.UserRole;
import org.abondar.experimental.wsboard.datamodel.task.Task;
import org.abondar.experimental.wsboard.datamodel.task.TaskState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class MapperTest {
    private static Logger logger = LoggerFactory.getLogger(MapperTest.class);

    @Autowired
    private DataMapper mapper;

    @Test
    public void insertUserTest() {
        logger.info("User insert test");

        var user = createUser();
        assertTrue(user.getId() > 0);

        cleanData();
    }

    @Test
    public void insertProjectTest() {
        logger.info("Insert project test");

        var project = createProject();
        assertTrue(project.getId() > 0);

        cleanData();
    }


    @Test
    public void insertUpdateContributorTest() {
        logger.info("Insert contributor test");

        var user = createUser();
        var project = createProject();

        var contributor = createContributor(user.getId(), project.getId(), false);
        assertTrue(contributor.getId() > 0);

        cleanData();
    }

    @Test
    public void insertUpdateTaskTest() {
        logger.info("Insert task test");

        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        assertTrue(task.getId() > 0);

        cleanData();
    }

    @Test
    public void updateUserTest() {
        logger.info("Update user test");

        var user = createUser();

        var newLogin = "login1";
        user.setLogin(newLogin);
        mapper.updateUser(user);

        user = mapper.getUserById(user.getId());

        assertEquals(newLogin, user.getLogin());

        cleanData();
    }

    @Test
    public void updateProjectTest() {
        logger.info("Update project test");

        var project = createProject();
        var newName = "name1";
        project.setName(newName);

        mapper.updateProject(project);

        project = mapper.getProjectById(project.getId());

        assertEquals(newName, project.getName());

        cleanData();
    }

    @Test
    public void updateAvatarTest() {
        logger.info("Update user avatar test");

        var user = createUser();
        var img = new byte[1024];

        user.setAvatar(img);
        mapper.updateUser(user);

        user = mapper.getUserById(user.getId());

        assertNotNull(user.getAvatar());

        cleanData();
    }

    @Test
    public void getUserByLoginTest() {
        logger.info("Get user by login test");

        var user = createUser();
        var res = mapper.getUserByLogin(user.getLogin());

        assertEquals(user.getId(), res.getId());
        cleanData();
    }


    @Test
    public void getUserByIdTest() {
        logger.info("Get user by login test");

        var user = createUser();
        var res = mapper.getUserById(user.getId());

        assertEquals(user.getId(), res.getId());
        cleanData();
    }


    @Test
    public void getProjectByIdTest() {
        logger.info("Get project by id test");

        var project = createProject();

        var res = mapper.getProjectById(project.getId());
        assertEquals(project.getId(), res.getId());

        cleanData();
    }

    @Test
    public void getProjectByNameTest() {
        logger.info("Get project by name test");

        var project = createProject();

        var res = mapper.getProjectByName(project.getName());
        assertEquals(project.getId(), res.getId());

        cleanData();
    }

    @Test
    public void getProjectOwnerTest() {
        logger.info("Get project owner test");

        var user = createUser();
        var project = createProject();
        createContributor(user.getId(), project.getId(), true);

        var res = mapper.getProjectOwner(project.getId());
        assertEquals(user.getId(), res.getId());

        cleanData();
    }

    @Test
    public void getContributorByIdTest() {
        logger.info("Get contributor by id test");

        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);


        var res = mapper.getContributorById(ctr.getId());
        assertEquals(ctr.getId(), res.getId());

        cleanData();
    }

    @Test
    public void insertContributorTest() {
        logger.info("insert contributor test");

        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);

        assertTrue(ctr.getId() > 0);

        cleanData();
    }

    @Test
    public void getUserByContributorIdTest() {
        logger.info("Get user by contributor id test");

        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);

        var res = mapper.getUserByContributorId(ctr.getId());
        assertEquals(user.getId(), res.getId());

        cleanData();
    }

    @Test
    public void updateContributorTest() {
        logger.info("Update contributor test");

        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);

        ctr.setActive(false);
        mapper.updateContributor(ctr);

        var res = mapper.getContributorById(ctr.getId());

        assertEquals(false, res.isActive());

        cleanData();
    }

    @Test
    public void getContributorsForProjectTest() {
        logger.info("Get contributors for project test");

        var user = createUser();
        var project = createProject();
        createContributor(user.getId(), project.getId(), true);

        var project1 = createProject();
        var inactiveCtr = createContributor(user.getId(),project1.getId(),false);
        inactiveCtr.setActive(false);
        mapper.insertContributor(inactiveCtr);

        var res = mapper.getContributorsForProject(project.getId(), 0, 1);
        assertEquals(1, res.size());
        assertEquals(user.getId(), res.get(0).getId());

        cleanData();
    }

    @Test
    public void getContributorByUserId() {
        logger.info("Get contributor by user id");

        var user = createUser();
        var project = createProject();
        createContributor(user.getId(), project.getId(), true);

        var res = mapper.getContributorByUserId(user.getId());
        assertEquals(user.getId(), res.getUserId());

        cleanData();
    }

    @Test
    public void getTaskByIdTest() {
        logger.info("Get task  by id test");

        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        var res = mapper.getTaskById(task.getId());
        assertEquals(task.getId(), res.getId());

        cleanData();
    }

    @Test
    public void getTasksForProjectTest() {
        logger.info("Get tasks for project test");

        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        var res = mapper.getTasksForProject(project.getId(), 0, 1);
        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());

        cleanData();
    }


    @Test
    public void getTasksForContributorTest() {
        logger.info("Get tasks for contributor test");

        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        var res = mapper.getTasksForContributor(contributor.getId(), 0, 1);
        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());

        cleanData();
    }

    @Test
    public void getTasksForUserTest() {
        logger.info("Get tasks for user test");

        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        var res = mapper.getTasksForUser(user.getId(), 0, 1);
        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());

        cleanData();
    }

    @Test
    public void insertSprintTest() {
        logger.info("Insert sprint test");

        var sprint = createSprint();
        assertTrue(sprint.getId() > 0);

        cleanData();
    }

    @Test
    public void getSprintByIdTest() {
        logger.info("Get sprint by id test");

        var sprint = createSprint();
        var res = mapper.getSprintById(sprint.getId());
        assertEquals(sprint.getName(), res.getName());

        cleanData();
    }

    @Test
    public void getSprintByNameTest() {
        logger.info("Get sprint by name test");

        var sprint = createSprint();
        var res = mapper.getSprintByName(sprint.getName());
        assertEquals(sprint.getName(), res.getName());

        cleanData();
    }

    @Test
    public void getAllSprintsTest() {
        logger.info("Get all sprints test");

        createSprint();
        createSprint();
        createSprint();

        var sprints = mapper.getSprints(0, 3);
        assertEquals(3, sprints.size());

        cleanData();
    }

    @Test
    public void updateTaskTest() {
        logger.info("Update task  test");

        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());
        var storyPoints = 1;

        task.setStoryPoints(storyPoints);
        task.setTaskState(TaskState.Completed);
        mapper.updateTask(task);

        var res = mapper.getTaskById(task.getId());

        assertEquals(storyPoints, task.getStoryPoints());

        cleanData();
    }


    @Test
    public void updateTaskSprintTest() {
        logger.info("Update task sprint test");

        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());
        var sprint = createSprint();

        task.setSprintId(sprint.getId());
        mapper.updateTaskSprint(task.getId(), task.getSprintId());

        var res = mapper.getTaskById(task.getId());
        assertEquals(task.getSprintId(), res.getSprintId());

        cleanData();
    }

    @Test
    public void getTasksForSprintTest() {
        logger.info("Get tasks for sprint test");

        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());
        task.setTaskState(TaskState.Completed);
        var sprint = createSprint();

        task.setSprintId(sprint.getId());
        mapper.updateTaskSprint(task.getId(), sprint.getId());

        var res = mapper.getTasksForSprint(sprint.getId(), 0, 1);
        assertEquals(1,res.size());
        assertEquals(task.getId(), res.get(0).getId());

        cleanData();
    }

    @Test
    public void deleteProjectTest() {
        logger.info("Delete project test");
        var project = createProject();

        mapper.deleteProject(project.getId());
        logger.info("Deleted project with id:" + project.getId());

        var res = mapper.getProjectById(project.getId());
        assertNull(res);
    }

    @Test
    public void deleteTaskTest() {
        logger.info("Delete task test");

        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        mapper.deleteTask(task.getId());
        logger.info("Deleted task with id:" + task.getId());

        var res = mapper.getTaskById(task.getId());
        assertNull(res);

        cleanData();
    }

    @Test
    public void deleteSprintTest(){
        logger.info("Delete sprint test");
        var sprint = createSprint();

        mapper.deleteSprint(sprint.getId());
        logger.info("Deleted sprint with id:"+sprint.getId());

        var res = mapper.getSprintById(sprint.getId());
        assertNull(res);

        cleanData();
    }


    private User createUser() {
        var roles = UserRole.Developer + ":" + UserRole.QA;
        var user = new User("testUser", "test@email.com",
                "test", "test", "12345", roles);

        mapper.insertUser(user);
        logger.info("Created user with id:" + user.getId());
        return user;
    }

    private Project createProject() {
        var project = new Project("test", new Date());
        mapper.insertProject(project);

        logger.info("Created project with id:" + project.getId());
        return project;
    }

    private Contributor createContributor(long userId, long projectId, boolean isOwner) {
        var contributor = new Contributor(userId, projectId, isOwner);
        mapper.insertContributor(contributor);
        logger.info("Created contributor with id:" + contributor.getId());

        return contributor;
    }

    private Task createTask(long contributorId) {
        var task = new Task(contributorId, new Date(), true);
        mapper.insertTask(task);
        logger.info("Created task with id:" + task.getId());
        return task;
    }

    private Sprint createSprint() {
        var sprint = new Sprint("test", new Date(), new Date());
        mapper.insertSprint(sprint);
        logger.info("Created sprint with id:" + sprint.getId());
        return sprint;
    }

    private void cleanData() {
        mapper.deleteTasks();
        mapper.deleteSprints();
        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();
    }
}
