package org.abondar.experimental.wsboard.server.dao;

import org.abondar.experimental.wsboard.server.datamodel.Contributor;
import org.abondar.experimental.wsboard.server.datamodel.task.Task;
import org.abondar.experimental.wsboard.server.datamodel.task.TaskState;
import org.abondar.experimental.wsboard.server.exception.DataCreationException;
import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


public class TaskDaoTest extends DaoTest {

    @InjectMocks
    private TaskDao taskDao;


    @Test
    public void createTaskTest() throws Exception {
        when(contributorMapper.getContributorById(anyLong())).thenReturn(ctr);
        doNothing().when(mapper).insertTask(any(Task.class));

        var task = taskDao.createTask(ctr.getId(), tsk.getStartDate(), tsk.isDevOpsEnabled(),
                tsk.getTaskName(), tsk.getTaskDescription());

        assertEquals(0, task.getId());

    }


    @Test
    public void createTaskNoContributorTest() {
        when(contributorMapper.getContributorById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class, () ->
                taskDao.createTask(100, tsk.getStartDate(), tsk.isDevOpsEnabled(),
                        tsk.getTaskName(), tsk.getTaskDescription()));

    }


    @Test
    public void createTaskInactiveContributorTest() {
        ctr.setActive(false);
        when(contributorMapper.getContributorById(anyLong())).thenReturn(ctr);
        assertThrows(DataExistenceException.class, () ->
                taskDao.createTask(ctr.getId(), tsk.getStartDate(), tsk.isDevOpsEnabled(),
                        tsk.getTaskName(), tsk.getTaskDescription()));

    }


    @Test
    public void updateTaskContributorTest() throws Exception {
        var ctr1 = new Contributor(1, prj.getId(), false);

        when(mapper.getTaskById(anyLong())).thenReturn(tsk);
        when(contributorMapper.getContributorById(anyLong())).thenReturn(ctr1);
        doNothing().when(mapper).updateTask(tsk);

        var res = taskDao.updateTask(tsk.getId(), ctr1.getId(), true,
                null, null, null);

        assertEquals(ctr1.getId(), res.getContributorId());

    }


    @Test
    public void updateTaskNotExistsTest() {
        when(mapper.getTaskById(anyLong())).thenReturn(null);

        assertThrows(DataExistenceException.class, () ->
                taskDao.updateTask(100, 100L, null, null, null, null));
    }

    @Test
    public void updateTaskContributorNotExistsTest() {
        when(mapper.getTaskById(anyLong())).thenReturn(tsk);
        when(contributorMapper.getContributorById(anyLong())).thenReturn(null);

        assertThrows(DataExistenceException.class, () ->
                taskDao.updateTask(tsk.getId(), 100L, null, null, null, null));

    }


    @Test
    public void updateTaskStoryPointsTest() throws Exception {
        when(mapper.getTaskById(anyLong())).thenReturn(tsk);
        doNothing().when(mapper).updateTask(tsk);


        int storyPoints = 2;
        var res = taskDao.updateTask(tsk.getId(), null,
                null, storyPoints, null, null);

        assertEquals(storyPoints, res.getStoryPoints());

    }

    @Test
    public void updateTaskNameTest() throws Exception {
        when(mapper.getTaskById(anyLong())).thenReturn(tsk);
        doNothing().when(mapper).updateTask(tsk);

        var taskName = "new name";
        var res = taskDao.updateTask(tsk.getId(), null,
                null, null, taskName, null);

        assertEquals(taskName, res.getTaskName());

    }

    @Test
    public void updateTaskDescriptionTest() throws Exception {
        when(mapper.getTaskById(anyLong())).thenReturn(tsk);
        doNothing().when(mapper).updateTask(tsk);

        var descr = "newDescr";
        var res = taskDao.updateTask(tsk.getId(), null,
                null, null, null, descr);

        assertEquals(descr, res.getTaskDescription());

    }


    @Test
    public void updateTaskSprintTest() throws Exception {
        when(mapper.getTaskById(anyLong())).thenReturn(tsk);
        when(mapper.getSprintById(anyLong())).thenReturn(sp);
        doNothing().when(mapper).updateTaskSprint(anyLong(), anyLong());

        var res = taskDao.updateTaskSprint(tsk.getId(), sp.getId());

        assertEquals(sp.getId(), res.getSprintId());

    }

    @Test
    public void updateTasksSprintTest() throws Exception {
        var tasks = List.of(tsk.getId(), 1L);

        when(mapper.getSprintById(anyLong())).thenReturn(sp);
        doNothing().when(mapper).updateTasksSprint(tasks, sp.getId());
        when(mapper.getTaskById(anyLong())).thenReturn(tsk);

        taskDao.updateTasksSprint(tasks, sp.getId());

        var res = taskDao.getTaskById(tsk.getId());
        assertEquals(sp.getId(), res.getSprintId());

    }


    @Test
    public void updateTaskStateTest() throws Exception {
        when(mapper.getTaskById(anyLong())).thenReturn(tsk);
        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        when(contributorMapper.getContributorById(anyLong())).thenReturn(ctr);
        doNothing().when(mapper).updateTask(tsk);

        var res = taskDao.updateTaskState(tsk.getId(), TaskState.IN_DEVELOPMENT.name());

        assertEquals(TaskState.IN_DEVELOPMENT, res.getTaskState());
        assertEquals(TaskState.CREATED, res.getPrevState());

    }


    @Test
    public void updateTaskStateUnknownTest() {
        assertThrows(DataExistenceException.class, () ->
                taskDao.updateTaskState(tsk.getId(), "test"));

    }

    @Test
    public void updateTaskStateNotExistsTest() {
        when(mapper.getTaskById(anyLong())).thenReturn(null);

        assertThrows(DataExistenceException.class, () ->
                taskDao.updateTaskState(100, TaskState.IN_DEPLOYMENT.name()));

    }

    @Test
    public void updateTaskStateAlreadyCompletedTest() {
        tsk.setTaskState(TaskState.COMPLETED);
        when(mapper.getTaskById(anyLong())).thenReturn(tsk);

        assertThrows(DataCreationException.class, () ->
                taskDao.updateTaskState(tsk.getId(), TaskState.IN_DEVELOPMENT.name()));

    }

    @Test
    public void updateTaskStateAlreadyCreatedTest() {
        when(mapper.getTaskById(anyLong())).thenReturn(tsk);

        assertThrows(DataCreationException.class, () ->
                taskDao.updateTaskState(tsk.getId(), TaskState.CREATED.name()));

    }

    @Test
    public void updateTaskStatePausedTest() {
        tsk.setTaskState(TaskState.PAUSED);
        when(mapper.getTaskById(anyLong())).thenReturn(tsk);

        assertThrows(DataCreationException.class, () ->
                taskDao.updateTaskState(tsk.getId(), TaskState.IN_DEVELOPMENT.name()));

    }


    @Test
    public void updateTaskStateNoDevOpsTest() {
        when(mapper.getTaskById(anyLong())).thenReturn(tsk);

        assertThrows(DataCreationException.class, () ->
                taskDao.updateTaskState(tsk.getId(), TaskState.IN_DEPLOYMENT.name()));

    }

    @Test
    public void updateTaskStateNoMovesTest() {
        when(mapper.getTaskById(anyLong())).thenReturn(tsk);
        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        when(contributorMapper.getContributorById(anyLong())).thenReturn(ctr);

        assertThrows(DataCreationException.class, () ->
                taskDao.updateTaskState(tsk.getId(), TaskState.IN_CODE_REVIEW.name()));

    }

    @Test
    public void updateTaskStateRoleUpdateNeededTest() {
        tsk.setTaskState(TaskState.IN_CODE_REVIEW);

        when(mapper.getTaskById(anyLong())).thenReturn(tsk);
        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        when(contributorMapper.getContributorById(anyLong())).thenReturn(ctr);

        assertThrows(DataCreationException.class, () ->
                taskDao.updateTaskState(tsk.getId(), TaskState.IN_TEST.name()));

    }

    @Test
    public void updateTaskStateCompletedEndDateTest() throws Exception {
        tsk.setTaskState(TaskState.IN_TEST);

        when(mapper.getTaskById(anyLong())).thenReturn(tsk);
        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        when(contributorMapper.getContributorById(anyLong())).thenReturn(ctr);
        doNothing().when(mapper).updateTask(tsk);

        var res = taskDao.updateTaskState(tsk.getId(), TaskState.COMPLETED.name());

        assertEquals(TaskState.COMPLETED, res.getTaskState());
        assertNotNull(res.getEndDate());

    }

    @Test
    public void deleteTaskTest() {
        when(mapper.getTaskById(anyLong())).thenReturn(tsk);
        doNothing().when(mapper).deleteTask(anyLong());

        var res = taskDao.deleteTask(tsk.getId());

        assertTrue(res);

    }


    @Test
    public void deleteTaskNoExistsTest() {
        when(mapper.getTaskById(anyLong())).thenReturn(null);

        var res = taskDao.deleteTask(100);

        assertFalse(res);
    }

    @Test
    public void deleteTaskInSprintTest(){
        tsk.setSprintId(sp.getId());

        when(mapper.getTaskById(anyLong())).thenReturn(tsk);

        var res = taskDao.deleteTask(tsk.getId());

        assertTrue(res);

    }


    @Test
    public void getTaskByIdTest() throws Exception {
        when(mapper.getTaskById(anyLong())).thenReturn(tsk);

        var res = taskDao.getTaskById(tsk.getId());

        assertEquals(tsk.getId(), res.getId());

    }

    @Test
    public void getTasksForProjectTest() throws Exception {
        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getTasksForProject(prj.getId(),0,1)).thenReturn(List.of(tsk));

        var res = taskDao.getTasksForProject(prj.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(tsk.getId(), res.get(0).getId());

    }

    @Test
    public void getNonSprintTasksForProjectTest() throws Exception {
        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getTasksForProject(prj.getId(),0,null)).thenReturn(List.of(tsk));

        var res = taskDao.getNonSprintTasksForProject(prj.getId());

        assertEquals(1, res.size());
        assertEquals(tsk.getId(), res.get(0).getId());

    }

    @Test
    public void getTasksForContributorTest() throws Exception {
        when(contributorMapper.getContributorById(anyLong())).thenReturn(ctr);
        when(mapper.getTasksForContributor(ctr.getId(),0,1)).thenReturn(List.of(tsk));

        var res = taskDao.getTasksForContributor(ctr.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(tsk.getId(), res.get(0).getId());


    }

    @Test
    public void countContributorTasksTest() throws Exception {
        when(contributorMapper.getContributorById(anyLong())).thenReturn(ctr);
        when(mapper.countContributorTasks(anyLong())).thenReturn(1);

        var res = taskDao.countContributorTasks(ctr.getId());

        assertEquals(Integer.valueOf(1), res);

    }

    @Test
    public void countSprintTasksTest() throws Exception {
        tsk.setSprintId(sp.getId());

        when(mapper.getSprintById(anyLong())).thenReturn(sp);
        when(mapper.countSprintTasks(anyLong())).thenReturn(1);

        var res = taskDao.countSprintTasks(sp.getId());

        assertEquals(Integer.valueOf(1), res);

    }

    @Test
    public void countSprintNotFoundTasksTest() {
        when(mapper.getSprintById(anyLong())).thenReturn(null);

        assertThrows(DataExistenceException.class, () -> {
            taskDao.countSprintTasks(100L);
        });

    }

    @Test
    public void countContributorNotFoundTasks() {
        when(contributorMapper.getContributorById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class, () -> taskDao.countContributorTasks(100L));
    }


    @Test
    public void getTasksForUserTest() throws Exception {
        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getTasksForUser(usr.getId(),0,1)).thenReturn(List.of(tsk));

        var res = taskDao.getTasksForUser(usr.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(tsk.getId(), res.get(0).getId());

    }

    @Test
    public void countUserTasks() throws Exception {
        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.countUserTasks(anyLong())).thenReturn(1);

        var res = taskDao.countUserTasks(usr.getId());

        assertEquals(Integer.valueOf(1), res);

    }

    @Test
    public void countUserNotFoundTasks() {
        when(userMapper.getUserById(anyLong())).thenReturn(null);

        assertThrows(DataExistenceException.class, () -> taskDao.countUserTasks(100L));
    }

    @Test
    public void getTasksForSprintTest() throws Exception {
        tsk.setSprintId(sp.getId());

        when(mapper.getSprintById(anyLong())).thenReturn(sp);
        when(mapper.getTasksForSprint(sp.getId(),0,1)).thenReturn(List.of(tsk));

        var res = taskDao.getTasksForSprint(sp.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(tsk.getId(), res.get(0).getId());

    }
}
