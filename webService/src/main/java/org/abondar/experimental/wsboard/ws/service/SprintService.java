package org.abondar.experimental.wsboard.ws.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.abondar.experimental.wsboard.datamodel.Sprint;

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

/**
 * Sprint CRUD web service
 *
 * @author a.bondar
 */
@Path("/sprint")
@Api("Sprint api")
public interface SprintService {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Create sprint",
            notes = "Creates a new sprint by name start and end date",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sprint created", response = Sprint.class),
            @ApiResponse(code = 205, message = "Sprint end date is wrong"),
            @ApiResponse(code = 206, message = "Form data is not complete or wrong"),
            @ApiResponse(code = 302, message = "Sprint with name already exists")
    })
    Response createSprint(@FormParam("name") @ApiParam(required = true) String name,
                          @FormParam("startDate") @ApiParam(required = true) String startDate,
                          @FormParam("endDate") @ApiParam(required = true) String endDate,
                          @FormParam("projectId") @ApiParam(required = true) long projectId);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Update sprint",
            notes = "Update sprint name,start and end date",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sprint updated", response = Sprint.class),
            @ApiResponse(code = 205, message = "Sprint end date is wrong"),
            @ApiResponse(code = 206, message = "Form data is not complete or wrong"),
            @ApiResponse(code = 302, message = "Sprint with name already exists"),
            @ApiResponse(code = 404, message = "Sprint with id not exists"),
            @ApiResponse(code = 409, message = "Project already has an active sprint"),
    })
    Response updateSprint(@PathParam("id") @ApiParam(required = true) long sprintId,
                          @FormParam("name") @ApiParam String name,
                          @FormParam("startDate") @ApiParam String startDate,
                          @FormParam("endDate") @ApiParam String endDate,
                          @FormParam("isCurrent") @ApiParam Boolean isCurrent);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find",
            notes = "Find sprint by id",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sprint with selected id", response = Sprint.class),
            @ApiResponse(code = 404, message = "Sprint with id not exists")
    })
    Response getSprintById(@PathParam("id") @ApiParam(required = true) long sprintId);

    @GET
    @Path("/current/project/{prId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find current",
            notes = "Find current sprint for project",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Current sprint for project", response = Sprint.class),
            @ApiResponse(code = 204, message = "No active sprints found"),
            @ApiResponse(code = 404, message = "Project not exists")
    })
    Response getCurrentSprint(@PathParam("prId") @ApiParam(required = true) long projectId);


    @GET
    @Path("/all/project/{prId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find sprints",
            notes = "Find sprints for project",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of existing sprints", response = Sprint.class),
            @ApiResponse(code = 204, message = "No sprints are found"),
            @ApiResponse(code = 404, message = "Project not found")
    })
    Response getSprints(
            @PathParam("prId") @ApiParam(required = true) long projectId,
            @QueryParam("offset") @ApiParam(required = true) int offset,
            @QueryParam("limit") @ApiParam(required = true) int limit);


    @GET
    @Path("/count/project/{prId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Count sprints",
            notes = "Count sprints for project",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of existing sprints", response = Integer.class),
            @ApiResponse(code = 404, message = "Project not found")
    })
    Response countSprints(@PathParam("prId") @ApiParam(required = true) long projectId);

    @DELETE
    @Path("/{id}")
    @ApiOperation(
            value = "Delete",
            notes = "Delete Sprint by id",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sprint is deleted"),
            @ApiResponse(code = 404, message = "Sprint with id not exists")
    })
    Response deleteSprint(@PathParam("id") @ApiParam(required = true) long sprintId);

}
