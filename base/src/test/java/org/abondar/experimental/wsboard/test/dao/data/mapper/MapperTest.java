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
import java.util.List;

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
        cleanData();
        var user = createUser();
        assertTrue(user.getId() > 0);
    }

    @Test
    public void insertProjectTest() {
        cleanData();
        var project = createProject();
        assertTrue(project.getId() > 0);
    }


    @Test
    public void insertUpdateContributorTest() {
        cleanData();
        var user = createUser();
        var project = createProject();

        var contributor = createContributor(user.getId(), project.getId(), false);
        assertTrue(contributor.getId() > 0);
    }

    @Test
    public void insertUpdateTaskTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        assertTrue(task.getId() > 0);
    }

    @Test
    public void updateUserTest() {
        cleanData();
        var user = createUser();

        var newLogin = "login1";
        user.setLogin(newLogin);
        mapper.updateUser(user);

        user = mapper.getUserById(user.getId());

        assertEquals(newLogin, user.getLogin());
    }

    @Test
    public void updateProjectTest() {
        cleanData();
        var project = createProject();
        var newName = "name1";
        project.setName(newName);

        mapper.updateProject(project);

        project = mapper.getProjectById(project.getId());

        assertEquals(newName, project.getName());

    }

    @Test
    public void updateAvatarTest() {
        var user = createUser();
        var img = "data/:base64,";

        user.setAvatar(img);
        mapper.updateUser(user);

        user = mapper.getUserById(user.getId());

        assertNotNull(user.getAvatar());

        cleanData();
    }

    @Test
    public void getUserByLoginTest() {
        cleanData();
        var user = createUser();
        var res = mapper.getUserByLogin(user.getLogin());

        assertEquals(user.getId(), res.getId());
    }


    @Test
    public void getUserByIdTest() {
        cleanData();
        var user = createUser();
        var res = mapper.getUserById(user.getId());

        assertEquals(user.getId(), res.getId());
    }

    @Test
    public void getUsersByIdsTest() {
        cleanData();
        var user = createUser();
        var user1 = createUser();
        var res = mapper.getUsersByIds(List.of(user.getId(), user1.getId()));

        assertEquals(2, res.size());
    }


    @Test
    public void getProjectByIdTest() {
        cleanData();
        var project = createProject();

        var res = mapper.getProjectById(project.getId());
        assertEquals(project.getId(), res.getId());
    }

    @Test
    public void getProjectByNameTest() {
        cleanData();
        var project = createProject();

        var res = mapper.getProjectByName(project.getName());
        assertEquals(project.getId(), res.getId());

    }

    @Test
    public void getProjectOwnerTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        createContributor(user.getId(), project.getId(), true);

        var res = mapper.getProjectOwner(project.getId());
        assertEquals(user.getId(), res.getId());

    }

    @Test
    public void getUserProjectTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var project1 = createProject();
        createContributor(user.getId(), project.getId(), true);
        createContributor(user.getId(), project1.getId(), true);

        var res = mapper.getUserProjects(user.getId());
        assertEquals(2, res.size());

    }

    @Test
    public void getContributorByIdTest() {

        cleanData();
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);


        var res = mapper.getContributorById(ctr.getId());
        assertEquals(ctr.getId(), res.getId());
    }

    @Test
    public void insertContributorTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);

        assertTrue(ctr.getId() > 0);

    }

    @Test
    public void insertCodeTest() {
        cleanData();
        var user = createUser();

        var code = new SecurityCode(123345, user.getId());
        mapper.insertCode(code);

        assertTrue(code.getId() > 0);

    }

    @Test
    public void deleteCodeTest() {
        cleanData();
        var user = createUser();

        var code = new SecurityCode(123345, user.getId());
        mapper.insertCode(code);

        mapper.deleteCode(code.getId());
        code = mapper.getCodeByUserId(user.getId());

        assertNull(code);

    }

    @Test
    public void checkCodeExistsTest() {
        cleanData();
        var user = createUser();

        var code = new SecurityCode(123345, user.getId());
        mapper.insertCode(code);

        Integer exists = mapper.checkCodeExists(code.getCode());

        assertEquals(Integer.valueOf(1), exists);

    }


    @Test
    public void updateContributorTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);

        ctr.setActive(false);
        mapper.updateContributor(ctr);

        var res = mapper.getContributorById(ctr.getId());

        assertFalse(res.isActive());
    }

    @Test
    public void getContributorByUserAndProjectTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);

        var res = mapper.getContributorByUserAndProject(user.getId(), project.getId());
        assertEquals(ctr.getId(), res.getId());

    }

    @Test
    public void getContributorsForProjectTest() {
        cleanData();
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

    }


    @Test
    public void countProjectContributorsTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        createContributor(user.getId(), project.getId(), true);

        var res = mapper.countProjectContributors(project.getId());
        assertEquals(Integer.valueOf(1), res);

    }

    @Test
    public void getContributorsByUserId() {
        cleanData();
        var user = createUser();
        var project = createProject();
        createContributor(user.getId(), project.getId(), true);

        var res = mapper.getContributorsByUserId(user.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(user.getId(), res.get(0).getUserId());

    }

    @Test
    public void deactivateUserContributorsTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), false);

        mapper.deactivateUserContributors(user.getId());
        var res = mapper.getContributorById(ctr.getId());
        assertFalse(res.isActive());
    }

    @Test
    public void deactivateProjectContributorsTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), false);

        mapper.deactivateProjectContributors(project.getId());
        var res = mapper.getContributorById(ctr.getId());
        assertFalse(res.isActive());
    }


    @Test
    public void getTaskByIdTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        var res = mapper.getTaskById(task.getId());
        assertEquals(task.getId(), res.getId());
    }

    @Test
    public void getTasksForProjectTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        var res = mapper.getTasksForProject(project.getId(), 0, 1);
        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());

    }


    @Test
    public void getTasksForContributorTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        var res = mapper.getTasksForContributor(contributor.getId(), 0, 1);
        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());

    }

    @Test
    public void getTasksForUserTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        var res = mapper.getTasksForUser(user.getId(), 0, 1);
        assertEquals(1, res.size());
        assertEquals(task.getId(), res.get(0).getId());

    }

    @Test
    public void countUserTasksTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        createTask(contributor.getId());

        var res = mapper.countUserTasks(user.getId());

        assertEquals(Integer.valueOf(1), res);

    }

    @Test
    public void countContributorTasksTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        createTask(contributor.getId());

        var res = mapper.countContributorTasks(contributor.getId());

        assertEquals(Integer.valueOf(1), res);

    }

    @Test
    public void insertSprintTest() {
        cleanData();
        var sprint = createSprint(createProject().getId());
        assertTrue(sprint.getId() > 0);
    }

    @Test
    public void getSprintByIdTest() {
        cleanData();
        var sprint = createSprint(createProject().getId());
        var res = mapper.getSprintById(sprint.getId());
        assertEquals(sprint.getName(), res.getName());
    }

    @Test
    public void getSprintByNameTest() {
        cleanData();
        var sprint = createSprint(createProject().getId());
        var res = mapper.getSprintByName(sprint.getName());
        assertEquals(sprint.getName(), res.getName());

    }

    @Test
    public void getSprintsTest() {
        cleanData();
        var prId = createProject().getId();
        createSprint(prId);
        createSprint(prId);
        createSprint(prId);

        var sprints = mapper.getSprints(prId, 0, 3);
        assertEquals(3, sprints.size());

    }

    @Test
    public void getCurrentSprintTest(){
        cleanData();
        var prId = createProject().getId();
        var sprint = createSprint(prId);

        sprint.setCurrent(true);
        mapper.updateSprint(sprint);

        var res = mapper.getCurrentSprint(prId);

        assertTrue(res.isCurrent());
        assertEquals(sprint.getId(),res.getId());
    }


    @Test
    public void updateTaskTest() {
        cleanData();
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

    }


    @Test
    public void updateTaskSprintTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());
        var sprint = createSprint(project.getId());

        task.setSprintId(sprint.getId());
        mapper.updateTaskSprint(task.getId(), task.getSprintId());

        var res = mapper.getTaskById(task.getId());
        assertEquals(task.getSprintId(), res.getSprintId());

    }

    @Test
    public void updateTasksSprintTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());
        var sprint = createSprint(project.getId());

        task.setSprintId(sprint.getId());
        mapper.updateTasksSprint(List.of(task.getId()), task.getSprintId());

        var res = mapper.getTaskById(task.getId());
        assertEquals(task.getSprintId(), res.getSprintId());

    }

    @Test
    public void getTasksForSprintTest() {
        cleanData();
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

    }

    @Test
    public void countSprintsTest() {
        cleanData();
        var prId = createProject().getId();
        createSprint(prId);

        var res = mapper.countSprints(prId);

        assertEquals(Integer.valueOf(1), res);

    }

    @Test
    public void deleteProjectTest() {
        cleanData();
        var project = createProject();

        mapper.deleteProject(project.getId());

        var res = mapper.getProjectById(project.getId());
        assertNull(res);
    }

    @Test
    public void deleteTaskTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        mapper.deleteTask(task.getId());

        var res = mapper.getTaskById(task.getId());
        assertNull(res);

    }

    @Test
    public void deleteSprintTest() {
        cleanData();
        var sprint = createSprint(createProject().getId());

        mapper.deleteSprint(sprint.getId());

        var res = mapper.getSprintById(sprint.getId());
        assertNull(res);

    }

    @Test
    public void deleteProjectTasks() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        mapper.deleteProjectTasks(project.getId());

        var res = mapper.getTaskById(task.getId());
        assertNull(res);

    }

    @Test
    public void deleteProjectSprints() {
        cleanData();
        var project = createProject();
        var sprint = createSprint(project.getId());

        mapper.deleteProjectSprints(project.getId());

        var res = mapper.getSprintById(sprint.getId());
        assertNull(res);

    }

    @Test
    public void deleteProjectContributors() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);

        mapper.deleteProjectContributors(project.getId());

        var res = mapper.getContributorById(contributor.getId());
        assertNull(res);

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
        var sprint = new Sprint("test", new Date(), new Date(), projectId);
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
