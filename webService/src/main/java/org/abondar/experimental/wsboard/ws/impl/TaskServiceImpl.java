package org.abondar.experimental.wsboard.ws.impl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(
            summary = "Create task",
            description = "Create a new task by contributor id,start date,devOps requirement",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task created",
                            content = @Content(schema = @Schema(implementation = Task.class))),
                    @ApiResponse(responseCode = "404", description = "Contributor not found"),
                    @ApiResponse(responseCode = "206", description = "Start date not parsed"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response createTask(@FormParam("ctrId")
                               @Parameter(description = "contributor id", required = true)
                                       long contributorId,
                               @FormParam("startDate")
                               @Parameter(description = "task start date", required = true)
                                       String startDate,
                               @FormParam("devOps")
                               @Parameter(description = "dev ops status", required = true)
                                           boolean devOpsEnabled) {

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
    @Operation(
            summary = "Update task",
            description = "Update an existing task's contributor,devops requirement and story points",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task updated",
                            content = @Content(schema = @Schema(implementation = Task.class))),
                    @ApiResponse(responseCode = "404", description = "Task or contributor not found"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response updateTask(@FormParam("id")
                               @Parameter(description = "task id", required = true)
                                       long taskId,
                               @FormParam("ctrId")
                               @Parameter(description = "contributor id")
                                       Long contributorId,
                               @FormParam("devOps")
                               @Parameter(description = "dev ops status")
                                       boolean devOpsEnabled,
                               @FormParam("storyPoints")
                               @Parameter(description = "story points")
                                       Integer storyPoints) {
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
    @Operation(
            summary = "Update task sprint",
            description = "Update an existing task's sprint",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task updated",
                            content = @Content(schema = @Schema(implementation = Task.class))),
                    @ApiResponse(responseCode = "404", description = "Task or sprint not found"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response updateTaskSprint(@FormParam("id")
                                     @Parameter(description = "task id", required = true)
                                             long taskId,
                                     @FormParam("sprintId")
                                     @Parameter(description = "sprint id", required = true)
                                             long sprintId) {
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
    @Operation(
            summary = "Update task state",
            description = "Update an existing task's state",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task updated",
                            content = @Content(schema = @Schema(implementation = Task.class))),
                    @ApiResponse(responseCode = "201", description = "Task is already created"),
                    @ApiResponse(responseCode = "202", description = "Task contributor must be changed"),
                    @ApiResponse(responseCode = "204", description = "Dev ops is not enabled for task"),
                    @ApiResponse(responseCode = "302", description = "Task is already completed"),
                    @ApiResponse(responseCode = "400", description = "Task state unknown"),
                    @ApiResponse(responseCode = "404", description = "Task not found"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong"),
                    @ApiResponse(responseCode = "409", description = "Task is returned to a wrong state after pause"),
                    @ApiResponse(responseCode = "501", description = "Task can't be changed to the state")

            }
    )
    @Override
    public Response updateTaskState(@FormParam("id")
                                    @Parameter(description = "task id", required = true)
                                            long taskId,
                                    @FormParam("state")
                                    @Parameter(description = "task state")
                                            String state) {

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
    @Operation(
            summary = "Delete task",
            description = "Delete task by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task deleted"),
                    @ApiResponse(responseCode = "404", description = "Task not found"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response deleteTask(@QueryParam("id") @Parameter(description = "task id", required = true) long id) {
        if (taskDao.deleteTask(id)) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Find task",
            description = "Find task by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task found",
                            content = @Content(schema = @Schema(implementation = Task.class))),
                    @ApiResponse(responseCode = "404", description = "Task  not found"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response getTaskById(@QueryParam("id") @Parameter(description = "task id", required = true) long taskId) {
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
    @Operation(
            summary = "Find project tasks",
            description = "Find tasks assigned to a project",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tasks found",
                            content = @Content(schema = @Schema(implementation = Task.class))),
                    @ApiResponse(responseCode = "404", description = "Project not found"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response getTasksForProject(@QueryParam("prId")
                                       @Parameter(description = "project id", required = true) long projectId,
                                       @QueryParam("offset")
                                       @Parameter(description = "Start of the range", required = true) int offset,
                                       @QueryParam("limit")
                                       @Parameter(description = "Number of tasks", required = true)
                                               int limit) {
        try {
            var tasks = taskDao.getTasksForProject(projectId, offset, limit);
            return Response.ok(tasks).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());

            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }

    }

    @GET
    @Path("/find_contributor_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Find contributor tasks",
            description = "Find tasks assigned to a contributor",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tasks found",
                            content = @Content(schema = @Schema(implementation = Task.class))),
                    @ApiResponse(responseCode = "404", description = "Contributor not found"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response getTasksForContributor(@QueryParam("ctrId")
                                           @Parameter(description = "contributor id", required = true)
                                                   long ctrId,
                                           @QueryParam("offset")
                                           @Parameter(description = "Start of the range", required = true)
                                                   int offset,
                                           @QueryParam("limit")
                                           @Parameter(description = "Number of tasks", required = true)
                                                   int limit) {
        try {
            var tasks = taskDao.getTasksForContributor(ctrId, offset, limit);
            return Response.ok(tasks).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());

            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }

    }

    @GET
    @Path("/find_user_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Find user tasks",
            description = "Find tasks assigned to a user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tasks found",
                            content = @Content(schema = @Schema(implementation = Task.class))),
                    @ApiResponse(responseCode = "404", description = "Contributor not found"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response getTasksForUser(@QueryParam("usrId")
                                    @Parameter(description = "user id", required = true)
                                            long usrId,
                                    @QueryParam("offset")
                                    @Parameter(description = "Start of the range", required = true)
                                            int offset,
                                    @QueryParam("limit")
                                    @Parameter(description = "Number of tasks", required = true)
                                            int limit) {

        try {
            var tasks = taskDao.getTasksForUser(usrId, offset, limit);
            return Response.ok(tasks).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());

            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }

    @GET
    @Path("/find_sprint_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Find sprint tasks",
            description = "Find tasks assigned in a sprint",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tasks found",
                            content = @Content(schema = @Schema(implementation = Task.class))),
                    @ApiResponse(responseCode = "404", description = "Contributor not found"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response getTasksForSprint(@QueryParam("spId")
                                      @Parameter(description = "sprint id", required = true)
                                              long sprintId,
                                      @QueryParam("offset")
                                      @Parameter(description = "Start of the range", required = true)
                                              int offset,
                                      @QueryParam("limit")
                                      @Parameter(description = "Number of tasks", required = true)
                                              int limit) {
        try {
            var tasks = taskDao.getTasksForSprint(sprintId, offset, limit);
            return Response.ok(tasks).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());

            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }
}
