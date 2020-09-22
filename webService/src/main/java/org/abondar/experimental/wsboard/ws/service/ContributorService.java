package org.abondar.experimental.wsboard.ws.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.user.User;

import javax.ws.rs.Consumes;
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
 * Contributor CRUD web service
 *
 * @author a.bondar
 */
@Path("/contributor")
@Api("Contributor api")
public interface ContributorService {

    @POST
    @Path("/user/{usrId}/project/{prjId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Create contributor",
            notes = "Create a new contributor by user id ,project id with owner or non-owner status",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Contributor created", response = Contributor.class),
            @ApiResponse(code = 404, message = "User or project not found"),
            @ApiResponse(code = 301, message = "Project is not active"),
            @ApiResponse(code = 302, message = "Contributor for project already exists for such user"),
            @ApiResponse(code = 409, message = "Project has owner(if isOwner param is set to true)"),

    })
    Response createContributor(@PathParam("usrId") @ApiParam(required = true) long userId,
                               @PathParam("prjId") @ApiParam(required = true) long projectId,
                               @FormParam("isOwner") @ApiParam(required = true) boolean isOwner);

    @PUT
    @Path("/user/{usrId}/project/{prjId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Update contributor",
            notes = "Update contributor owner and active status",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Contributor updated", response = Contributor.class),
            @ApiResponse(code = 404, message = "Contributor,Project,User not found"),
            @ApiResponse(code = 302, message = "Project has owner"),
            @ApiResponse(code = 403, message = "Contributor can't be deactivated"),
            @ApiResponse(code = 409, message = "Project has no owner"),
            @ApiResponse(code = 410, message = "Contributor not active and can't be an owner"),
    })
    Response updateContributor(@PathParam("usrId") @ApiParam(required = true) long userId,
                               @PathParam("prjId") @ApiParam(required = true) long projectId,
                               @FormParam("isOwner") @ApiParam Boolean isOwner,
                               @FormParam("isActive") @ApiParam Boolean isActive);

    @GET
    @Path("/project/{projectId}/owner")
    @ApiOperation(
            value = "Find Project Owner",
            notes = "Find a user who is a project owner",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project owner", response = User.class),
            @ApiResponse(code = 204, message = "Project has no owner"),
            @ApiResponse(code = 404, message = "Project not found")})
    @Produces(MediaType.APPLICATION_JSON)
    Response findProjectOwner(@PathParam("projectId") @ApiParam(required = true) long projectId);


    @GET
    @Path("/project/{projectId}")
    @ApiOperation(
            value = "Find Project Contributors",
            notes = "Find a list of project contributors",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project contributors", response = User.class),
            @ApiResponse(code = 204, message = "No contributors found"),
            @ApiResponse(code = 404, message = "Project not found")
    })
    Response findProjectContributors(@PathParam("projectId") @ApiParam(required = true) long projectId,
                                     @QueryParam("offset") @ApiParam(required = true) int offset,
                                     @QueryParam("limit") @ApiParam(required = true) int limit);


    @GET
    @Path("/project/{projectId}/count")
    @ApiOperation(
            value = "Count Project Contributors",
            notes = "Counts number of project contributors",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project contributors counted", response = Integer.class),
            @ApiResponse(code = 404, message = "Project not found")
    })
    Response countProjectContributors(@PathParam("projectId") @ApiParam(required = true) long projectId);

    @GET
    @Path("/user/{userId}")
    @ApiOperation(
            value = "Find User Contributors",
            notes = "Find a list of contributors related to userId",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "P contributors", response = Contributor.class),
            @ApiResponse(code = 204, message = "No contributors found"),
            @ApiResponse(code = 404, message = "User not found")
    })
    Response findContributorsByUserId(@PathParam("userId") @ApiParam(required = true) long userId,
                                      @QueryParam("offset") @ApiParam(required = true) int offset,
                                      @QueryParam("limit") @ApiParam(required = true) int limit);

    @GET
    @Path("/user/{userId}/project/{projectId}")
    @ApiOperation(
            value = "Find Contributor",
            notes = "Find a single contributor by userId and projectId",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project contributors", response = Long.class),
            @ApiResponse(code = 404, message = "User or Project not found")
    })
    Response findProjectContributor(@PathParam("userId") @ApiParam(required = true) long userId,
                             @PathParam("projectId") @ApiParam(required = true) long projectId);

    @GET
    @Path("/login/{login}/project/{projectId}")
    @ApiOperation(
            value = "Find Contributor by name",
            notes = "Find a single contributor by name",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project contributor", response = Contributor.class),
            @ApiResponse(code = 404, message = "User or Project not found")
    })
    Response findContributorByLogin(@PathParam("projectId") @ApiParam(required = true) long projectId,
                                    @PathParam("login") @ApiParam(required = true) String login);
}
