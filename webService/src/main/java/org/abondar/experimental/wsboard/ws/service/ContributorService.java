package org.abondar.experimental.wsboard.ws.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.abondar.experimental.wsboard.datamodel.Contributor;

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
 * Contributor CRUD web service
 *
 * @author a.bondar
 */
@Path("/contributor")
@Api("Contributor api")
public interface ContributorService extends RestService {

    @POST
    @Path("/create")
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
            @ApiResponse(code = 406, message = "JWT token is wrong"),
            @ApiResponse(code = 409, message = "Project has owner(if isOwner param is set to true)"),

    })
    Response createContributor(@FormParam("userId") @ApiParam(required = true) long userId,
                               @FormParam("projectId") @ApiParam(required = true) long projectId,
                               @FormParam("isOwner") @ApiParam(required = true) boolean isOwner);

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Update contributor",
            notes = "Update contributor owner and active status",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Contributor created", response = Contributor.class),
            @ApiResponse(code = 404, message = "Contributor not found"),
            @ApiResponse(code = 302, message = "Project has owner"),
            @ApiResponse(code = 403, message = "Contributor can't be deactivated"),
            @ApiResponse(code = 406, message = "JWT token is wrong"),
            @ApiResponse(code = 409, message = "Project has no owner"),
            @ApiResponse(code = 410, message = "Contributor not active and can't be an owner"),
    })
    Response updateContributor(@FormParam("ctrId") @ApiParam(required = true) long contributorId,
                               @FormParam("isOwner") @ApiParam Boolean isOwner,
                               @FormParam("isActive") @ApiParam Boolean isActive);

    @GET
    @Path("/find_project_owner")
    @ApiOperation(
            value = "Find Project Owner",
            notes = "Find a user who is a project owner",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project owner", response = Contributor.class),
            @ApiResponse(code = 204, message = "Project has no owner"),
            @ApiResponse(code = 404, message = "Project not found"),
            @ApiResponse(code = 406, message = "JWT token is wrong")})
    Response findProjectOwner(@QueryParam("projectId") @ApiParam(required = true) long projectId);


    @GET
    @Path("/find_project_contributors")
    @ApiOperation(
            value = "Find Project Contributors",
            notes = "Find a list of project contributors",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project contributors", response = Contributor.class),
            @ApiResponse(code = 204, message = "No contributors found"),
            @ApiResponse(code = 404, message = "Project not found"),
            @ApiResponse(code = 406, message = "JWT token is wrong"),
    })
    Response findProjectContributors(@QueryParam("projectId") @ApiParam(required = true) long projectId,
                                     @QueryParam("offset") @ApiParam(required = true) int offset,
                                     @QueryParam("limit") @ApiParam(required = true) int limit);
}
