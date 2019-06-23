package org.abondar.experimental.wsboard.ws.impl;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.abondar.experimental.wsboard.dao.TaskDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.task.Task;
import org.abondar.experimental.wsboard.ws.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * Task web service implementation
 *
 * @author a.bondar
 */
@Path("/task")
public class TaskServiceImpl implements TaskService {

    private static Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    @Qualifier("taskDao")
    private TaskDao taskDao;


    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Create task",
            notes = "Create a new task by contributor id,start date,devOps requirement",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Task created", response = Task.class),
            @ApiResponse(code = 404, message = "Contributor not found"),
            @ApiResponse(code = 206, message = "Start date not parsed"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    })
    @Override
    public Response createTask(@FormParam("ctrId") @ApiParam(required = true) long contributorId,
                               @FormParam("startDate") @ApiParam(required = true) String startDate,
                               @FormParam("devOps") @ApiParam(required = true) boolean devOpsEnabled) {

        try {
            Date stDate = convertDate(startDate);
            var task = taskDao.createTask(contributorId, stDate, devOpsEnabled);
            return Response.ok(task).build();

        } catch (DataCreationException ex) {
            logger.error(ex.getMessage());

            return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }

    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Update task",
            notes = "Update an existing task's contributor,devops requirement and story points",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Task updated", response = Task.class),
            @ApiResponse(code = 404, message = "Task or contributor not found"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    })
    @Override
    public Response updateTask(@FormParam("id") @ApiParam(required = true) long taskId,
                               @FormParam("ctrId") @ApiParam Long contributorId,
                               @FormParam("devOps") @ApiParam boolean devOpsEnabled,
                               @FormParam("storyPoints") @ApiParam Integer storyPoints) {
        try {
            var task = taskDao.updateTask(taskId, contributorId, devOpsEnabled, storyPoints);
            return Response.ok(task).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();

        }

    }

    @POST
    @Path("/update_sprint")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Update task sprint",
            notes = "Update an existing task's sprint",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Task updated", response = Task.class),
            @ApiResponse(code = 404, message = "Task or sprint not found"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    })
    @Override
    public Response updateTaskSprint(@FormParam("id") @ApiParam(required = true) long taskId,
                                     @FormParam("sprintId") @ApiParam(required = true) long sprintId) {
        try {
            var task = taskDao.updateTaskSprint(taskId, sprintId);
            return Response.ok(task).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());

            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }

    @POST
    @Path("/update_state")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Update task state",
            notes = "Update an existing task's state",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Task updated", response = Task.class),
            @ApiResponse(code = 201, message = "Task is already created"),
            @ApiResponse(code = 202, message = "Task contributor must be changed"),
            @ApiResponse(code = 204, message = "Dev ops is not enabled for task"),
            @ApiResponse(code = 302, message = "Task is already completed"),
            @ApiResponse(code = 400, message = "Task state unknown"),
            @ApiResponse(code = 404, message = "Task not found"),
            @ApiResponse(code = 406, message = "JWT token is wrong"),
            @ApiResponse(code = 409, message = "Task is returned to a wrong state after pause"),
            @ApiResponse(code = 501, message = "Task can't be changed to the state")
    })
    @Override
    public Response updateTaskState(@FormParam("id") @ApiParam(required = true) long taskId,
                                    @FormParam("state") @ApiParam String state) {

        try {
            var task = taskDao.updateTaskState(taskId, state);
            return Response.ok(task).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());

            if (ex.getMessage().equals(LogMessageUtil.TASK_NOT_EXISTS)) {
                return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getLocalizedMessage()).build();
            }
        } catch (DataCreationException ex) {
            logger.error(ex.getMessage());

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

    @GET
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Delete task",
            notes = "Delete task by id",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Task deleted"),
            @ApiResponse(code = 404, message = "Task not found"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    })
    @Override
    public Response deleteTask(@QueryParam("id") @ApiParam(required = true) long id) {
        if (taskDao.deleteTask(id)) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find task",
            notes = "Find task by id",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Task found", response = Task.class),
            @ApiResponse(code = 404, message = "Task  not found"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    })
    @Override
    public Response getTaskById(@QueryParam("id") @ApiParam(required = true) long taskId) {
        try {
            var task = taskDao.getTaskById(taskId);
            return Response.ok(task).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());

            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }

    }

    @GET
    @Path("/find_project_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find project tasks",
            notes = "Find tasks assigned to a project",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tasks found", response = Task.class),
            @ApiResponse(code = 204, message = "No tasks not found"),
            @ApiResponse(code = 404, message = "Project not found"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    })
    @Override
    public Response getTasksForProject(@QueryParam("prId") @ApiParam(required = true) long projectId,
                                       @QueryParam("offset") @ApiParam(required = true) int offset,
                                       @QueryParam("limit") @ApiParam(required = true)
                                               int limit) {
        try {
            var tasks = taskDao.getTasksForProject(projectId, offset, limit);
            if (tasks.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            return Response.ok(tasks).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());

            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }

    }

    @GET
    @Path("/find_contributor_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find contributor tasks",
            notes = "Find tasks assigned to a contributor",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tasks found", response = Task.class),
            @ApiResponse(code = 204, message = "No tasks not found"),
            @ApiResponse(code = 404, message = "Contributor not found"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    })
    @Override
    public Response getTasksForContributor(@QueryParam("ctrId") @ApiParam(required = true) long ctrId,
                                           @QueryParam("offset") @ApiParam(required = true) int offset,
                                           @QueryParam("limit") @ApiParam(required = true) int limit) {
        try {
            var tasks = taskDao.getTasksForContributor(ctrId, offset, limit);
            if (tasks.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            return Response.ok(tasks).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());

            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }

    }

    @GET
    @Path("/find_user_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find user tasks",
            notes = "Find tasks assigned to a user",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tasks found", response = Task.class),
            @ApiResponse(code = 204, message = "No tasks not found"),
            @ApiResponse(code = 404, message = "Contributor not found"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    })
    @Override
    public Response getTasksForUser(@QueryParam("usrId") @ApiParam(required = true) long usrId,
                                    @QueryParam("offset") @ApiParam(required = true) int offset,
                                    @QueryParam("limit") @ApiParam(required = true) int limit) {

        try {
            var tasks = taskDao.getTasksForUser(usrId, offset, limit);
            if (tasks.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            return Response.ok(tasks).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());

            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }

    @GET
    @Path("/find_sprint_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find sprint tasks",
            notes = "Find tasks assigned in a sprint",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tasks found", response = Task.class),
            @ApiResponse(code = 204, message = "No tasks not found"),
            @ApiResponse(code = 404, message = "Contributor not found"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    }
    )
    @Override
    public Response getTasksForSprint(@QueryParam("spId") @ApiParam(required = true) long sprintId,
                                      @QueryParam("offset") @ApiParam(required = true) int offset,
                                      @QueryParam("limit") @ApiParam(required = true) int limit) {
        try {
            var tasks = taskDao.getTasksForSprint(sprintId, offset, limit);
            if (tasks.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            return Response.ok(tasks).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());

            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }
}
