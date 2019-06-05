package org.abondar.experimental.wsboard.test.ws.impl;

import org.abondar.experimental.wsboard.ws.service.TaskService;

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
 * Test implementation of task web service
 */
@Path("/task")
public class TaskServiceTestImpl implements TaskService {

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response createTask(@FormParam("ctrId") long contributorId,
                               @FormParam("startDate") String startDate,
                               @FormParam("devOps") String devOpsEnabled) {
        return null;
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateTask(@FormParam("id") long taskId,
                               @FormParam("ctrId") Long contributorId,
                               @FormParam("devOps") String devOpsEnabled,
                               @FormParam("storyPoints") Integer storyPoints) {
        return null;
    }

    @POST
    @Path("/update_sprint")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateTaskSprint(@FormParam("id") long taskId,
                                     @FormParam("sprintId") long sprintId) {
        return null;
    }

    @POST
    @Path("/update_state")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateTaskState(@FormParam("id") long taskId,
                                    @FormParam("state") String state) {
        return null;
    }

    @GET
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response deleteTask(@QueryParam("id") long id) {
        return null;
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getTaskById(@QueryParam("id") long taskId) {
        return null;
    }

    @GET
    @Path("/find_project_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getTasksForProject(@QueryParam("prId") long projectId,
                                       @QueryParam("offset") int offset,
                                       @QueryParam("limit") int limit) {
        return null;
    }

    @GET
    @Path("/find_contributor_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getTasksForContributor(@QueryParam("ctrId") long ctrId,
                                           @QueryParam("offset") int offset,
                                           @QueryParam("limit") int limit) {
        return null;
    }

    @GET
    @Path("/find_user_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getTasksForUser(@QueryParam("usrId") long usrId,
                                    @QueryParam("offset") int offset,
                                    @QueryParam("limit") int limit) {
        return null;
    }

    @GET
    @Path("/find_sprint_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getTasksForSprint(@QueryParam("spId") long sprintId,
                                      @QueryParam("offset") int offset,
                                      @QueryParam("limit") int limit) {
        return null;
    }
}
