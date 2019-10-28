package org.abondar.experimental.wsboard.ws.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.abondar.experimental.wsboard.datamodel.task.Task;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Task CRUD web service
 *
 * @author a.bondar
 */
@Path("/task")
@Api("Task api")
public interface TaskService {

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
            @ApiResponse(code = 206, message = "Start date not parsed")
    })
    Response createTask(@FormParam("ctrId") @ApiParam(required = true) long contributorId,
                        @FormParam("startDate") @ApiParam(required = true) String startDate,
                        @FormParam("devOps") @ApiParam(required = true) boolean devOpsEnabled,
                        @FormParam("taskName") @ApiParam(required = true) String taskName,
                        @FormParam("taskDescription") @ApiParam(required = true) String taskDescription);

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
            @ApiResponse(code = 404, message = "Task or contributor not found")
    })
    Response updateTask(@FormParam("id") @ApiParam(required = true) long taskId,
                        @FormParam("ctrId") @ApiParam Long contributorId,
                        @FormParam("devOps") @ApiParam boolean devOpsEnabled,
                        @FormParam("storyPoints") @ApiParam Integer storyPoints,
                        @FormParam("taskName") @ApiParam(required = true) String taskName,
                        @FormParam("taskDescription") @ApiParam(required = true) String taskDescription);


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
            @ApiResponse(code = 404, message = "Task or sprint not found")
    })
    Response updateTaskSprint(@FormParam("id") @ApiParam(required = true) long taskId,
                              @FormParam("sprintId") @ApiParam(required = true) long sprintId);

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
            @ApiResponse(code = 409, message = "Task is returned to a wrong state after pause"),
            @ApiResponse(code = 501, message = "Task can't be changed to the state")
    })
    Response updateTaskState(@FormParam("id") @ApiParam(required = true) long taskId,
                             @FormParam("state") @ApiParam String state);

    @GET
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Delete task",
            notes = "Delete task by id",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Task deleted"),
            @ApiResponse(code = 404, message = "Task not found")
    })
    Response deleteTask(@QueryParam("id") @ApiParam(required = true) long id);

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find task",
            notes = "Find task by id",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Task found", response = Task.class),
            @ApiResponse(code = 404, message = "Task  not found")
    })
    Response getTaskById(@QueryParam("id") @ApiParam(required = true) long taskId);

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
            @ApiResponse(code = 404, message = "Project not found")
    })
    Response getTasksForProject(@QueryParam("prId") @ApiParam(required = true) long projectId,
                                @QueryParam("offset") @ApiParam(required = true) int offset,
                                @QueryParam("limit") @ApiParam(required = true) int limit);

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
            @ApiResponse(code = 404, message = "Contributor not found")
    })
    Response getTasksForContributor(@QueryParam("ctrId") @ApiParam(required = true) long ctrId,
                                    @QueryParam("offset") @ApiParam(required = true) int offset,
                                    @QueryParam("limit") @ApiParam(required = true) int limit);


    @GET
    @Path("/count_contributor_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Count contributor tasks",
            notes = "Count number of tasks assigned to a contributor",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tasks counted", response = Task.class),
            @ApiResponse(code = 404, message = "Contributor not found")
    })
    Response countContributorTasks(@QueryParam("contributorId") @ApiParam(required = true) long ctrId);

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
            @ApiResponse(code = 404, message = "Contributor not found")
    })
    Response getTasksForUser(@QueryParam("usrId") @ApiParam(required = true) long usrId,
                             @QueryParam("offset") @ApiParam(required = true) int offset,
                             @QueryParam("limit") @ApiParam(required = true) int limit);


    @GET
    @Path("/count_user_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Count user tasks",
            notes = "Count number of tasks assigned to a user",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Tasks counted", response = Task.class),
            @ApiResponse(code = 404, message = "Contributor not found")
    })
    Response countUserTasks(@QueryParam("userId") @ApiParam(required = true) long ctrId);



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
            @ApiResponse(code = 404, message = "Contributor not found")
    })
    Response getTasksForSprint(@QueryParam("spId") @ApiParam(required = true) long sprintId,
                               @QueryParam("offset") @ApiParam(required = true) int offset,
                               @QueryParam("limit") @ApiParam(required = true) int limit);

}
