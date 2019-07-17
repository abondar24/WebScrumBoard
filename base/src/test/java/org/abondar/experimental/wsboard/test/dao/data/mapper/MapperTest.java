package org.abondar.experimental.wsboard.test.dao.data.mapper;

import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.SecurityCode;
import org.abondar.experimental.wsboard.datamodel.Sprint;
import org.abondar.experimental.wsboard.datamodel.task.Task;
import org.abondar.experimental.wsboard.datamodel.task.TaskState;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@SpringBootTest(classes = WebScrumBoardApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class MapperTest {

    @Autowired
    private DataMapper mapper;

    @Test
    public void insertUserTest() {
        var user = createUser();
        assertTrue(user.getId() > 0);

        cleanData();
    }

    @Test
    public void insertProjectTest() {
        var project = createProject();
        assertTrue(project.getId() > 0);

        cleanData();
    }


    @Test
    public void insertUpdateContributorTest() {
        var user = createUser();
        var project = createProject();

        var contributor = createContributor(user.getId(), project.getId(), false);
        assertTrue(contributor.getId() > 0);

        cleanData();
    }

    @Test
    public void insertUpdateTaskTest() {
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        assertTrue(task.getId() > 0);

        cleanData();
    }

    @Test
    public void updateUserTest() {
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
        var user = createUser();
        var res = mapper.getUserByLogin(user.getLogin());

        assertEquals(user.getId(), res.getId());
        cleanData();
    }


    @Test
    public void getUserByIdTest() {
        var user = createUser();
        var res = mapper.getUserById(user.getId());

        assertEquals(user.getId(), res.getId());
        cleanData();
    }


    @Test
    public void getProjectByIdTest() {
        var project = createProject();

        var res = mapper.getProjectById(project.getId());
        assertEquals(project.getId(), res.getId());

        cleanData();
    }

    @Test
    public void getProjectByNameTest() {
        var project = createProject();

        var res = mapper.getProjectByName(project.getName());
        assertEquals(project.getId(), res.getId());

        cleanData();
    }

    @Test
    public void getProjectOwnerTest() {
        var user = createUser();
        var project = createProject();
        createContributor(user.getId(), project.getId(), true);

        var res = mapper.getProjectOwner(project.getId());
        assertEquals(user.getId(), res.getId());

        cleanData();
    }

    @Test
    public void getUserProjectTest(){
        var user = createUser();
        var project = createProject();
        var project1 = createProject();
        createContributor(user.getId(),project.getId(),true);
        createContributor(user.getId(),project1.getId(),true);

        var res = mapper.getUserProjects(user.getId());
        assertEquals(2,res.size());

        cleanData();
    }

    @Test
    public void getContributorByIdTest() {
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);


        var res = mapper.getContributorById(ctr.getId());
        assertEquals(ctr.getId(), res.getId());

        cleanData();
    }

    @Test
    public void insertContributorTest() {
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);

        assertTrue(ctr.getId() > 0);

        cleanData();
    }

    @Test
    public void insertCodeTest() {
        var user = createUser();

        var code = new SecurityCode(123345, user.getId());
        mapper.insertCode(code);

        assertTrue(code.getId() > 0);

        cleanData();
    }

    @Test
    public void updateCodeTest() {
        var user = createUser();

        var code = new SecurityCode(123345, user.getId());
        mapper.insertCode(code);

        mapper.updateCode(code.getId());

        code = mapper.getCodeByUserId(user.getId());

        assertTrue(code.isActivated());

        cleanData();
    }

    @Test
    public void checkCodeExistsTest() {
        var user = createUser();

        var code = new SecurityCode(123345, user.getId());
        mapper.insertCode(code);

        Integer exists = mapper.checkCodeExists(code.getCode());

        assertEquals(Integer.valueOf(1), exists);

        cleanData();
    }


    @Test
    public void updateContributorTest() {
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);

        ctr.setActive(false);
        mapper.updateContributor(ctr);

        var res = mapper.getContributorById(ctr.getId());

        assertFalse(res.isActive());

        cleanData();
    }

    @Test
    public void getContributorsForProjectTest() {
        var user = createUser();
        var project = createProject();
        createContributor(user.getId(), project.getId(), true);

        var project1 = createProject();
        var inactiveCtr = createContributor(user.getId(), project1.getId(), false);
        inactiveCtr.setActive(false);
        mapper.insertContributor(inactiveCtr);

        var res = mapper.getContributorsForProject(project.getId(), 0, 1);
        assertEquals(1, res.size());
        assertEquals(user.getId(), res.get(0).getId());

        cleanData();
    }

    @Test
    public void getContributorsByUserId() {
        var user = createUser();
        var project = createProject();
        createContributor(user.getId(), project.getId(), true);

        var res = mapper.getContributorsByUserId(user.getId(),0,1);

        assertEquals(1,res.size());
        assertEquals(user.getId(), res.get(0).getUserId());

        cleanData();
    }

    @Test
    public void deactivateUserContributorsTest(){
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), false);

        mapper.deactivateUserContributors(user.getId());
        var res = mapper.getContributorById(ctr.getId());
        assertFalse(res.isActive());
    }

    @Test
    public void deactivateProjectContributorsTest(){
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), false);

        mapper.deactivateProjectContributors(project.getId());
        var res = mapper.getContributorById(ctr.getId());
        assertFalse(res.isActive());
    }

    @Test
    public void getTaskByIdTest() {
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
        var sprint = createSprint(createProject().getId());
        assertTrue(sprint.getId() > 0);

        cleanData();
    }

    @Test
    public void getSprintByIdTest() {
        var sprint = createSprint(createProject().getId());
        var res = mapper.getSprintById(sprint.getId());
        assertEquals(sprint.getName(), res.getName());

        cleanData();
    }

    @Test
    public void getSprintByNameTest() {
        var sprint = createSprint(createProject().getId());
        var res = mapper.getSprintByName(sprint.getName());
        assertEquals(sprint.getName(), res.getName());

        cleanData();
    }

    @Test
    public void getSprintsTest() {
        var prId = createProject().getId();
        createSprint(prId);
        createSprint(prId);
        createSprint(prId);

        var sprints = mapper.getSprints(prId,0, 3);
        assertEquals(3, sprints.size());

        cleanData();
    }

    @Test
    public void updateTaskTest() {
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());
        var storyPoints = 1;

        task.setStoryPoints(storyPoints);
        task.setTaskState(TaskState.COMPLETED);
        mapper.updateTask(task);

        var res = mapper.getTaskById(task.getId());

        assertEquals(storyPoints, res.getStoryPoints());

        cleanData();
    }


    @Test
    public void updateTaskSprintTest() {
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());
        var sprint = createSprint(project.getId());

        task.setSprintId(sprint.getId());
        mapper.updateTaskSprint(task.getId(), task.getSprintId());

        var res = mapper.getTaskById(task.getId());
        assertEquals(task.getSprintId(), res.getSprintId());

        cleanData();
    }

    @Test
    public void getTasksForSprintTest() {
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());
        task.setTaskState(TaskState.COMPLETED);
        var sprint = createSprint(project.getId());

        task.setSprintId(sprint.getId());
        mapper.updateTaskSprint(task.getId(), sprint.getId());

        var res = mapper.getTasksForSprint(sprint.getId(), 0, 1);
        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());

        cleanData();
    }

    @Test
    public void deleteProjectTest() {
        var project = createProject();

        mapper.deleteProject(project.getId());

        var res = mapper.getProjectById(project.getId());
        assertNull(res);
    }

    @Test
    public void deleteTaskTest() {
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        mapper.deleteTask(task.getId());

        var res = mapper.getTaskById(task.getId());
        assertNull(res);

        cleanData();
    }

    @Test
    public void deleteSprintTest() {
        var sprint = createSprint(createProject().getId());

        mapper.deleteSprint(sprint.getId());

        var res = mapper.getSprintById(sprint.getId());
        assertNull(res);

        cleanData();
    }


    private User createUser() {
        var roles = UserRole.DEVELOPER + ":" + UserRole.QA;
        var user = new User("testUser", "test@email.com",
                "test", "test", "12345", roles);

        mapper.insertUser(user);
        return user;
    }

    private Project createProject() {
        var project = new Project("test", new Date());
        mapper.insertProject(project);
        return project;
    }

    private Contributor createContributor(long userId, long projectId, boolean isOwner) {
        var contributor = new Contributor(userId, projectId, isOwner);
        mapper.insertContributor(contributor);
        return contributor;
    }

    private Task createTask(long contributorId) {
        var task = new Task(contributorId, new Date(), true, "name", "descr");
        mapper.insertTask(task);
        return task;
    }

    private Sprint createSprint(long projectId) {
        var sprint = new Sprint("test", new Date(), new Date(),projectId);
        mapper.insertSprint(sprint);
        return sprint;
    }

    private void cleanData() {
        mapper.deleteTasks();
        mapper.deleteSprints();
        mapper.deleteContributors();
        mapper.deleteCodes();
        mapper.deleteUsers();
        mapper.deleteProjects();
    }
}
