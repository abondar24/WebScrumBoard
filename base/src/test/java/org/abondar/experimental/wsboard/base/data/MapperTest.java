package org.abondar.experimental.wsboard.base.data;

import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.datamodel.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.Assert.*;


@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class MapperTest {
    private static Logger logger = LoggerFactory.getLogger(MapperTest.class);

    @Autowired
    private DataMapper mapper;

    @Test
    public void insertUpdateUserTest() {
        logger.info("User insert test");

        var user = createUser();
        assertTrue(user.getId() > 0);

        mapper.deleteUsers();
    }

    @Test
    public void insertUpdateProjectTest() {
        logger.info("Insert project test");

        var project = createProject();
        assertTrue(project.getId() > 0);

        mapper.deleteProjects();
    }


    @Test
    public void insertUpdateContributorTest() {
        logger.info("Insert contributor test");

        var user = createUser();
        var project = createProject();

        var contributor = createContributor(user.getId(), project.getId(), false);
        assertTrue(contributor.getId() > 0);

        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();
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
    public void updateAvatarTest(){
        logger.info("Update user avatar test");

        var user = createUser();
        var img = new byte[1024];

        user.setAvatar(img);
        mapper.updateUserAvatar(user.getId(),user.getAvatar());

        user = mapper.getUserById(user.getId());

        assertNotNull(user.getAvatar());

        mapper.deleteUsers();
    }

    @Test
    public void getUserByLoginTest() {
        logger.info("Get user by login test");

        var user = createUser();
        var res = mapper.getUserByLogin(user.getLogin());

        assertEquals(user.getId(), res.getId());
        mapper.deleteUsers();
    }


    @Test
    public void getUserByIdTest() {
        logger.info("Get user by login test");

        var user = createUser();
        var res = mapper.getUserById(user.getId());

        assertEquals(user.getId(), res.getId());
        mapper.deleteUsers();
    }


    @Test
    public void getProjectByIdTest() {
        logger.info("Get project by id test");

        var project = createProject();

        var res = mapper.getProjectById(project.getId());
        assertEquals(project.getId(), res.getId());

        mapper.deleteProjects();
    }

    @Test
    public void getProjectByNameTest() {
        logger.info("Get project by name test");

        var project = createProject();

        var res = mapper.getProjectByName(project.getName());
        assertEquals(project.getId(), res.getId());

        mapper.deleteProjects();
    }

    @Test
    public void getProjectOwnerTest() {
        logger.info("Get project owner test");

        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), true);

        var res = mapper.getProjectOwner(project.getId());
        assertEquals(user.getId(), res.getId());

        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();
    }


    @Test
    public void getContributorsForProjectTest() {
        logger.info("Get contributors for project test");

        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), true);

        var res = mapper.getContributorsForProject(project.getId(), 0, 1);
        assertEquals(1, res.size());
        assertEquals(user.getId(), res.get(0).getId());

        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();
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
    public void getTasksForuserTest() {
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
    public void deleteProjectTest() {
        logger.info("Delete project test");
        var project = createProject();

        mapper.deleteProject(project.getId());
        logger.info("Deleted project with id:" + project.getId());

        var res = mapper.getProjectById(project.getId());
        assertNull(res);
    }

    @Test
    public void deleteContributorTest() {
        logger.info("Delete contributor test");

        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);

        mapper.deleteContributor(contributor.getId());
        logger.info("Deleted contributor with id:" + contributor.getId());

        var res = mapper.getContributorsForProject(project.getId(), 0, 1);
        assertEquals(0, res.size());

        mapper.deleteUsers();
        mapper.deleteProjects();
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

        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();
    }

    private User createUser() {
        var roles = UserRole.Developer + ":" + UserRole.Manager;
        var user = new User("testUser", "test@email.com",
                "test", "test", "12345", roles);

        mapper.insertUpdateUser(user);
        logger.info("Created user with id:" + user.getId());
        return user;
    }

    private Project createProject() {
        var project = new Project("test", new Date());
        mapper.insertUpdateProject(project);

        logger.info("Created project with id:" + project.getId());
        return project;
    }

    private Contributor createContributor(long userId, long projectId, boolean isOwner) {
        var contributor = new Contributor(userId, projectId, isOwner);
        mapper.insertUpdateContributor(contributor);
        logger.info("Created contributor with id:" + contributor.getId());

        return contributor;
    }

    private Task createTask(long contributorId) {
        var task = new Task(contributorId, TaskState.Created, 1, new Date(), new Date());
        mapper.insertUpdateTask(task);
        logger.info("Created task with id:" + task.getId());
        return task;
    }

    private void cleanData() {
        mapper.deleteTasks();
        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();
    }
}
