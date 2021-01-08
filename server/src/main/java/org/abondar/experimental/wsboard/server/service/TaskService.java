package org.abondar.experimental.wsboard.server.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.abondar.experimental.wsboard.server.datamodel.task.Task;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Task CRUD web service
 *
 * @author a.bondar
 */
@Path("/task")
@Api("Task api")
public interface TaskService {

    @POST
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
            @ApiResponse(code = 206, message = "Start date not parsed")
    })
    Response createTask(@FormParam("ctrId") @ApiParam(required = true) long contributorId,
                        @FormParam("startDate") @ApiParam(required = true) String startDate,
                        @FormParam("devOps") @ApiParam(required = true) boolean devOpsEnabled,
                        @FormParam("taskName") @ApiParam(required = true) String taskName,
                        @FormParam("taskDescription") @ApiParam(required = true) String taskDescription);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Update task",
            notes = "Update an existing task's contributor,devops requirement and story points",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Task updated", response = Task.class),
            @ApiResponse(code = 404, message = "Task or contributor not found")
    })
    Response updateTask(@PathParam("id") @ApiParam(required = true) long taskId,
                        @FormParam("ctrId") @ApiParam Long contributorId,
                        @FormParam("devOps") @ApiParam boolean devOpsEnabled,
                        @FormParam("storyPoints") @ApiParam Integer storyPoints,
                        @FormParam("taskName") @ApiParam(required = true) String taskName,
                        @FormParam("taskDescription") @ApiParam(required = true) String taskDescription);


    @PUT
    @Path("/{id}/sprint/{sprintId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Update task sprint",
            notes = "Update an existing task's sprint",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Task updated", response = Task.class),
            @ApiResponse(code = 404, message = "Task or sprint not found")
    })
    Response updateTaskSprint(@PathParam("id") @ApiParam(required = true) long taskId,
                              @PathParam("sprintId") @ApiParam(required = true) long sprintId);


    @PUT
    @Path("/sprint/{sprintId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Update tasks sprint",
            notes = "Update sprint for several tasks",
            consumes = "application/json",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tasks updated"),
            @ApiResponse(code = 404, message = "Sprint not found")
    })
    Response updateTasksSprint(@QueryParam("id") @ApiParam(required = true) List<Long> ids,
                               @PathParam("sprintId") @ApiParam(required = true) long sprintId);

    @PUT
    @Path("/{id}/state")
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
            @ApiResponse(code = 409, message = "Task is returned to a wrong state after pause"),
            @ApiResponse(code = 501, message = "Task can't be changed to the state")
    })
    Response updateTaskState(@PathParam("id") @ApiParam(required = true) long taskId,
                             @FormParam("state") @ApiParam String state);


    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Delete task",
            notes = "Delete task by id",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Task deleted"),
            @ApiResponse(code = 404, message = "Task not found")
    })
    Response deleteTask(@PathParam("id") @ApiParam(required = true) long id);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find task",
            notes = "Find task by id",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Task found", response = Task.class),
            @ApiResponse(code = 404, message = "Task  not found")
    })
    Response getTaskById(@PathParam("id") @ApiParam(required = true) long taskId);

    @GET
    @Path("/project/{prId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find project tasks",
            notes = "Find tasks assigned to a project",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tasks found", response = Task.class),
            @ApiResponse(code = 204, message = "No tasks not found"),
            @ApiResponse(code = 404, message = "Project not found")
    })
    Response getTasksForProject(@PathParam("prId") @ApiParam(required = true) long projectId,
                                @QueryParam("offset") @ApiParam(required = true) int offset,
                                @QueryParam("limit") @ApiParam(required = true) int limit,
                                @QueryParam("all")@ApiParam(required = true) boolean all);

    @GET
    @Path("/contributor/{ctrId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find contributor tasks",
            notes = "Find tasks assigned to a contributor",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tasks found", response = Task.class),
            @ApiResponse(code = 204, message = "No tasks not found"),
            @ApiResponse(code = 404, message = "Contributor not found")
    })
    Response getTasksForContributor(@PathParam("ctrId") @ApiParam(required = true) long ctrId,
                                    @QueryParam("offset") @ApiParam(required = true) int offset,
                                    @QueryParam("limit") @ApiParam(required = true) int limit);


    @GET
    @Path("/contributor/{ctrId}/count")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Count contributor tasks",
            notes = "Count number of tasks assigned to a contributor",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tasks counted", response = Integer.class),
            @ApiResponse(code = 404, message = "Contributor not found")
    })
    Response countContributorTasks(@PathParam("ctrId") @ApiParam(required = true) long ctrId);

    @GET
    @Path("/user/{usrId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find user tasks",
            notes = "Find tasks assigned to a user",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tasks found", response = Task.class),
            @ApiResponse(code = 204, message = "No tasks not found"),
            @ApiResponse(code = 404, message = "User not found")
    })
    Response getTasksForUser(@PathParam("usrId") @ApiParam(required = true) long usrId,
                             @QueryParam("offset") @ApiParam(required = true) int offset,
                             @QueryParam("limit") @ApiParam(required = true) int limit);


    @GET
    @Path("/user/{usrId}/count")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Count user tasks",
            notes = "Count number of tasks assigned to a user",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tasks counted", response = Integer.class),
            @ApiResponse(code = 404, message = "User not found")
    })
    Response countUserTasks(@PathParam("usrId") @ApiParam(required = true) long ctrId);


    @GET
    @Path("/sprint/{spId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find sprint tasks",
            notes = "Find tasks assigned in a sprint",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tasks found", response = Task.class),
            @ApiResponse(code = 204, message = "No tasks not found"),
            @ApiResponse(code = 404, message = "Contributor not found")
    })
    Response getTasksForSprint(@PathParam("spId") @ApiParam(required = true) long sprintId,
                               @QueryParam("offset") @ApiParam(required = true) int offset,
                               @QueryParam("limit") @ApiParam(required = true) int limit);


    @GET
    @Path("/sprint/{spId}/count")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Count sprint tasks",
            notes = "Count number of tasks in a sprint",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tasks counted", response = Integer.class),
            @ApiResponse(code = 404, message = "Sprint not found")
    })
    Response countSprintTasks(@PathParam("spId") @ApiParam(required = true) long spId);
}
