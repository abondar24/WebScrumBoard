package org.abondar.experimental.wsboard.server.mapper;

import org.abondar.experimental.wsboard.server.datamodel.Contributor;
import org.abondar.experimental.wsboard.server.datamodel.Project;
import org.abondar.experimental.wsboard.server.datamodel.SecurityCode;
import org.abondar.experimental.wsboard.server.datamodel.Sprint;
import org.abondar.experimental.wsboard.server.datamodel.task.Task;
import org.abondar.experimental.wsboard.server.datamodel.task.TaskState;
import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.abondar.experimental.wsboard.server.datamodel.user.UserRole;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MapperTest {

    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected ProjectMapper projectMapper;
    @Autowired
    protected ContributorMapper contributorMapper;
    @Autowired
    protected SecurityCodeMapper securityCodeMapper;

    @Autowired
    protected SprintMapper sprintMapper;

    @Autowired
    private DataMapper mapper;

    @Test
    public void insertTaskTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        assertTrue(task.getId() > 0);
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


    protected User createUser() {
        var roles = UserRole.DEVELOPER + ":" + UserRole.QA;
        var user = new User("testUser", "test@email.com",
                "test", "test", "12345", roles);

        userMapper.insertUser(user);

        return user;
    }

    protected Project createProject() {
        var project = new Project("test", new Date());
        projectMapper.insertProject(project);
        return project;
    }

    protected Contributor createContributor(long userId, long projectId, boolean isOwner) {
        var contributor = new Contributor(userId, projectId, isOwner);
        contributorMapper.insertContributor(contributor);
        return contributor;
    }

    private Task createTask(long contributorId) {
        var task = new Task(contributorId, new Date(), true, "name", "descr");
        mapper.insertTask(task);
        return task;
    }

    protected Sprint createSprint(long projectId) {
        var sprint = new Sprint("test", new Date(), new Date(), projectId);
        sprintMapper.insertSprint(sprint);
        return sprint;
    }

    protected void cleanData() {
        mapper.deleteTasks();
        mapper.deleteSprints();
        contributorMapper.deleteContributors();
        securityCodeMapper.deleteCodes();
        userMapper.deleteUsers();
        projectMapper.deleteProjects();
    }
}
