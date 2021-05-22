package org.abondar.experimental.wsboard.server.mapper;

import org.abondar.experimental.wsboard.server.datamodel.Contributor;
import org.abondar.experimental.wsboard.server.datamodel.Project;
import org.abondar.experimental.wsboard.server.datamodel.SecurityCode;
import org.abondar.experimental.wsboard.server.datamodel.Sprint;
import org.abondar.experimental.wsboard.server.datamodel.task.Task;
import org.abondar.experimental.wsboard.server.datamodel.task.TaskState;
import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.abondar.experimental.wsboard.server.datamodel.user.UserRole;
import org.apache.ibatis.session.SqlSession;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MapperTest {

    @Autowired
    private DataMapper mapper;

    @Autowired
    protected UserMapper userMapper;



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

    //TODO: move to ctr mapper test
    @Test
    @Ignore
    public void getProjectOwnerTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        createContributor(user.getId(), project.getId(), true);

        //var res = mapper.getProjectOwner(project.getId());
       // assertEquals(user.getId(), res.getId());

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

    //TODO: move to ctr mapper test
    @Test
    @Ignore
    public void getContributorsForProjectTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        createContributor(user.getId(), project.getId(), true);

        var project1 = createProject();
        var inactiveCtr = createContributor(user.getId(), project1.getId(), false);
        inactiveCtr.setActive(false);
        mapper.insertContributor(inactiveCtr);

       // var res = mapper.getContributorsForProject(project.getId(), 0, 1);
       // assertEquals(1, res.size());
       // assertEquals(user.getId(), res.get(0).getId());

    }

    @Test
    public void getContributorByNameTest() {
        cleanData();

        var user = createUser();
        var project = createProject();
        var ctr = createContributor(user.getId(), project.getId(), true);

        var res = mapper.getContributorByLogin(project.getId(), user.getLogin());
        assertEquals(ctr.getId(), res.getId());
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
    public void countSprintTasksTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var sprint = createSprint(project.getId());
        var task = createTask(contributor.getId());

        mapper.updateTaskSprint(task.getId(), sprint.getId());
        var res = mapper.countSprintTasks(sprint.getId());

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
    public void getSprintsNullLimitTest() {
        cleanData();
        var prId = createProject().getId();
        createSprint(prId);
        createSprint(prId);
        createSprint(prId);

        var sprints = mapper.getSprints(prId, 0, null);
        assertEquals(3, sprints.size());

    }

    @Test
    public void getCurrentSprintTest() {
        cleanData();
        var prId = createProject().getId();
        var sprint = createSprint(prId);

        sprint.setCurrent(true);
        mapper.updateSprint(sprint);

        var res = mapper.getCurrentSprint(prId);

        assertTrue(res.isCurrent());
        assertEquals(sprint.getId(), res.getId());
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

    protected User createUser() {
        var roles = UserRole.DEVELOPER + ":" + UserRole.QA;
        var user = new User("testUser", "test@email.com",
                "test", "test", "12345", roles);

        userMapper.insertUser(user);

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

    protected void cleanData() {
        mapper.deleteTasks();
        mapper.deleteSprints();
        mapper.deleteContributors();
        mapper.deleteCodes();
        userMapper.deleteUsers();
        mapper.deleteProjects();
    }
}
