package org.abondar.experimental.wsboard.ws.impl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.abondar.experimental.wsboard.dao.ContributorDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.ws.service.ContributorService;
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

/**
 * Contributor service implementation
 *
 * @author a.bondar
 */
@Path("/contributor")
public class ContributorServiceImpl implements ContributorService {

    private static Logger logger = LoggerFactory.getLogger(ContributorService.class);

    @Autowired
    @Qualifier("contributorDao")
    private ContributorDao contributorDao;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Create contributor",
            description = "Create a new contributor by user id ,project id with owner or non-owner status",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Contributor created",
                            content = @Content(schema = @Schema(implementation = Contributor.class))),
                    @ApiResponse(responseCode = "404", description = "User or project not found"),
                    @ApiResponse(responseCode = "301", description = "Project is not active"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong"),
                    @ApiResponse(responseCode = "409", description = "Project has owner(if isOwner param is set to true)"),

            }
    )
    @Override
    public Response createContributor(@FormParam("userId")
                                      @Parameter(description = "User ID", required = true) long userId,
                                      @FormParam("projectId")
                                      @Parameter(description = "Project ID", required = true) long projectId,
                                      @FormParam("isOwner")
                                      @Parameter(description = "Is Owner", required = true) boolean isOwner) {

        try {
            var ctr = contributorDao.createContributor(userId, projectId, isOwner);
            return Response.ok(ctr).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ex) {
            logger.error(ex.getMessage());

            if (ex.getMessage().equals(LogMessageUtil.PROJECT_NOT_ACTIVE)) {
                return Response.status(Response.Status.MOVED_PERMANENTLY).entity(ex.getLocalizedMessage()).build();
            } else {
                return Response.status(Response.Status.CONFLICT).entity(ex.getLocalizedMessage()).build();
            }
        }

    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Update contributor",
            description = "Update contributor owner and active status",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Contributor created",
                            content = @Content(schema = @Schema(implementation = Contributor.class))),
                    @ApiResponse(responseCode = "404", description = "Contributor not found"),
                    @ApiResponse(responseCode = "302", description = "Project has owner"),
                    @ApiResponse(responseCode = "403", description = "Contributor can't be deactivated"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong"),
                    @ApiResponse(responseCode = "409", description = "Project has no owner"),
                    @ApiResponse(responseCode = "410", description = "Contributor not active and can't be an owner"),


            }
    )
    @Override
    public Response updateContributor(@FormParam("ctrId") long contributorId,
                                      @FormParam("isOwner") Boolean isOwner,
                                      @FormParam("isActive") Boolean isActive) {

        try {

            var ctr = contributorDao.updateContributor(contributorId, isOwner, isActive);
            return Response.ok(ctr).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ex) {
            logger.error(ex.getMessage());

            switch (ex.getMessage()) {
                case LogMessageUtil.PROJECT_HAS_OWNER:
                    return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
                case LogMessageUtil.PROJECT_HAS_NO_OWNER:
                    return Response.status(Response.Status.CONFLICT).entity(ex.getLocalizedMessage()).build();
                case LogMessageUtil.CONTRIBUTOR_NOT_ACTIVE:
                    return Response.status(Response.Status.GONE).entity(ex.getLocalizedMessage()).build();
                case LogMessageUtil.CONTRIBUTOR_CANNOT_BE_DEACTIVATED:
                    return Response.status(Response.Status.FORBIDDEN).entity(ex.getLocalizedMessage()).build();
            }

        }
        return null;
    }

    @GET
    @Path("/find_project_owner")
    @Operation(
            summary = "Find Project Owner",
            description = "Find a user who is a project owner",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project owner",
                            content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "204", description = "Project has no owner"),
                    @ApiResponse(responseCode = "404", description = "Project not found"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong"),

            }
    )
    @Override
    public Response findProjectOwner(@QueryParam("projectId")
                                     @Parameter(description = "Project Id", required = true) long projectId) {

        try {
            var owner = contributorDao.findProjectOwner(projectId);
            return Response.ok(owner).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NO_CONTENT).entity(ex.getLocalizedMessage()).build();
        }

    }

    @GET
    @Path("/find_project_contributors")
    @Operation(
            summary = "Find Project Contributors",
            description = "Find a list of project contributors",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project contributors",
                            content = @Content(schema = @Schema(implementation = Contributor.class))),
                    @ApiResponse(responseCode = "204", description = "No contributors found"),
                    @ApiResponse(responseCode = "404", description = "Project not found"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong"),

            }
    )
    @Override
    public Response findProjectContributors(@QueryParam("projectId") long projectId,
                                            @QueryParam("offset") int offset,
                                            @QueryParam("limit") int limit) {

        try {
            var contributors = contributorDao.findProjectContributors(projectId, offset, limit);
            if (contributors.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            return Response.ok(contributors).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());

            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }
}
