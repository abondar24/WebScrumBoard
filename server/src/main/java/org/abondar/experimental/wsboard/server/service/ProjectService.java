package org.abondar.experimental.wsboard.server.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.abondar.experimental.wsboard.server.datamodel.Project;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Project CRUD  web service
 *
 * @author a.bondar
 */
@Path("/project")
@Api("Project api")
public interface ProjectService {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Create project",
            notes = "Creates a new project by name and start date",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project created", response = Project.class),
            @ApiResponse(code = 206, message = "Form data is not complete"),
            @ApiResponse(code = 302, message = "Project with name already exists")
    })
    Response createProject(@FormParam("name") @ApiParam(required = true) String name,
                           @FormParam("startDate") @ApiParam(required = true) String startDate);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Update project",
            notes = "Update project name,repository,activity,end date",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project updated", response = Project.class),
            @ApiResponse(code = 205, message = "Wrong end date for project"),
            @ApiResponse(code = 206, message = "End date can't be parsed"),
            @ApiResponse(code = 301, message = "Project can't be reactivated"),
            @ApiResponse(code = 302, message = "Project with name already exists"),
            @ApiResponse(code = 404, message = "Project not found")
    })
    Response updateProject(@PathParam("id") @ApiParam(required = true) long id,
                           @FormParam("name") @ApiParam String name,
                           @FormParam("repo") @ApiParam String repo,
                           @FormParam("isActive") @ApiParam Boolean isActive,
                           @FormParam("endDate") @ApiParam String endDate,
                           @FormParam("description") @ApiParam String description);

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Delete",
            notes = "Delete project",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project deleted"),
            @ApiResponse(code = 404, message = "Project with id not exists")
    })
    Response deleteProject(@PathParam("id") @ApiParam(required = true) long id);


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find",
            notes = "Find project by id",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project found", response = Project.class),
            @ApiResponse(code = 404, message = "Project with id not exists")
    })
    Response findProjectById(@PathParam("id") @ApiParam(required = true) long id);

    @GET
    @Path("/user/{usrId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find User Projects",
            notes = "Find projects related to user",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project found", response = Project.class),
            @ApiResponse(code = 204, message = "No contributors found"),
            @ApiResponse(code = 404, message = "User id not exists")
    })
    Response findUserProjects(@PathParam("usrId") @ApiParam(required = true) long userId);


}
