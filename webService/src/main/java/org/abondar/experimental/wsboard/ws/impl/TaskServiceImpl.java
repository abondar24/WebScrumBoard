package org.abondar.experimental.wsboard.ws.impl;


import org.abondar.experimental.wsboard.dao.TaskDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.ws.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * Task web service implementation
 *
 * @author a.bondar
 */
public class TaskServiceImpl implements TaskService {


    @Autowired
    @Qualifier("taskDao")
    private TaskDao taskDao;



    @Override
    public Response createTask(long contributorId, String startDate, boolean devOpsEnabled) {

        try {
            Date stDate = convertDate(startDate);
            var task = taskDao.createTask(contributorId, stDate, devOpsEnabled);
            return Response.ok(task).build();

        } catch (DataCreationException ex) {
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }

    }


    @Override
    public Response updateTask(long taskId, Long contributorId, boolean devOpsEnabled, Integer storyPoints) {
        try {
            var task = taskDao.updateTask(taskId, contributorId, devOpsEnabled, storyPoints);
            return Response.ok(task).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();

        }

    }


    @Override
    public Response updateTaskSprint(long taskId, long sprintId) {
        try {
            var task = taskDao.updateTaskSprint(taskId, sprintId);
            return Response.ok(task).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }


    @Override
    public Response updateTaskState(long taskId, String state) {

        try {
            var task = taskDao.updateTaskState(taskId, state);
            return Response.ok(task).build();
        } catch (DataExistenceException ex) {
            if (ex.getMessage().equals(LogMessageUtil.TASK_NOT_EXISTS)) {
                return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getLocalizedMessage()).build();
            }
        } catch (DataCreationException ex) {
            switch (ex.getMessage()) {
                case LogMessageUtil.TASK_ALREADY_COMPLETED:
                    return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
                case LogMessageUtil.TASK_ALREADY_CREATED:
                    return Response.status(Response.Status.CREATED).entity(ex.getLocalizedMessage()).build();
                case LogMessageUtil.TASK_WRONG_STATE_AFTER_PAUSE:
                    return Response.status(Response.Status.CONFLICT).entity(ex.getLocalizedMessage()).build();
                case LogMessageUtil.TASK_DEV_OPS_NOT_ENABLED:
                    return Response.status(Response.Status.NO_CONTENT).entity(ex.getLocalizedMessage()).build();
                case LogMessageUtil.TASK_MOVE_NOT_AVAILABLE:
                    return Response.status(Response.Status.NOT_IMPLEMENTED).entity(ex.getLocalizedMessage()).build();
                case LogMessageUtil.TASK_CONTRIBUTOR_UPDATE:
                    return Response.status(Response.Status.ACCEPTED).entity(ex.getLocalizedMessage()).build();

            }
        }

        return null;
    }


    @Override
    public Response deleteTask(long id) {
        if (taskDao.deleteTask(id)) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response getTaskById(long taskId) {
        try {
            var task = taskDao.getTaskById(taskId);
            return Response.ok(task).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }

    }


    @Override
    public Response getTasksForProject(long projectId, int offset, int limit) {
        try {
            var tasks = taskDao.getTasksForProject(projectId, offset, limit);
            if (tasks.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            return Response.ok(tasks).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }

    }


    @Override
    public Response getTasksForContributor(long ctrId, int offset, int limit) {
        try {
            var tasks = taskDao.getTasksForContributor(ctrId, offset, limit);
            if (tasks.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            return Response.ok(tasks).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }

    }


    @Override
    public Response getTasksForUser(long usrId, int offset, int limit) {

        try {
            var tasks = taskDao.getTasksForUser(usrId, offset, limit);
            if (tasks.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            return Response.ok(tasks).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }


    @Override
    public Response getTasksForSprint(long sprintId, int offset, int limit) {
        try {
            var tasks = taskDao.getTasksForSprint(sprintId, offset, limit);
            if (tasks.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            return Response.ok(tasks).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }
}
