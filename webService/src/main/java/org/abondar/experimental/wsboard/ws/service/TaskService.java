package org.abondar.experimental.wsboard.ws.service;

import javax.ws.rs.core.Response;

/**
 * Task CRUD web service
 *
 * @author a.bondar
 */
public interface TaskService extends RestService {

    Response createTask(long contributorId, String startDate, boolean devOpsEnabled);

    Response updateTask(long taskId, Long contributorId, boolean devOpsEnabled, Integer storyPoints);

    Response updateTaskSprint(long taskId, long sprintId);

    Response updateTaskState(long taskId, String state);

    Response deleteTask(long id);

    Response getTaskById(long taskId);

    Response getTasksForProject(long projectId, int offset, int limit);

    Response getTasksForContributor(long ctrId, int offset, int limit);

    Response getTasksForUser(long usrId, int offset, int limit);

    Response getTasksForSprint(long sprintId, int offset, int limit);

}
