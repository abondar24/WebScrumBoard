package org.abondar.experimental.wsboard.server.mapper;

import org.abondar.experimental.wsboard.server.datamodel.task.TaskState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskMapperTest extends MapperTest{
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

        var res = taskMapper.getTaskById(task.getId());
        assertEquals(task.getId(), res.getId());
    }

    @Test
    public void getTasksForProjectTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        var res = taskMapper.getTasksForProject(project.getId(), 0, 1);
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

        var res = taskMapper.getTasksForContributor(contributor.getId(), 0, 1);
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

        var res = taskMapper.getTasksForUser(user.getId(), 0, 1);
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

        var res = taskMapper.countUserTasks(user.getId());

        assertEquals(Integer.valueOf(1), res);

    }

    @Test
    public void countContributorTasksTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        createTask(contributor.getId());

        var res = taskMapper.countContributorTasks(contributor.getId());

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

        taskMapper.updateTaskSprint(task.getId(), sprint.getId());
        var res = taskMapper.countSprintTasks(sprint.getId());

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
        taskMapper.updateTask(task);

        var res = taskMapper.getTaskById(task.getId());

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
        taskMapper.updateTaskSprint(task.getId(), task.getSprintId());

        var res = taskMapper.getTaskById(task.getId());
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
        taskMapper.updateTasksSprint(List.of(task.getId()), task.getSprintId());

        var res = taskMapper.getTaskById(task.getId());
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
        taskMapper.updateTaskSprint(task.getId(), sprint.getId());

        var res = taskMapper.getTasksForSprint(sprint.getId(), 0, 1);
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

        taskMapper.deleteTask(task.getId());

        var res = taskMapper.getTaskById(task.getId());
        assertNull(res);

    }


    @Test
    public void deleteProjectTasks() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var contributor = createContributor(user.getId(), project.getId(), false);
        var task = createTask(contributor.getId());

        taskMapper.deleteProjectTasks(project.getId());

        var res = taskMapper.getTaskById(task.getId());
        assertNull(res);

    }


}
