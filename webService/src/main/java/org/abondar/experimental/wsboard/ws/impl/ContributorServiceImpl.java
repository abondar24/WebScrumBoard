package org.abondar.experimental.wsboard.ws.impl;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.abondar.experimental.wsboard.dao.ContributorDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Contributor;
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

    }
    )
    @Override
    public Response createContributor(@FormParam("userId") @ApiParam(required = true) long userId,
                                      @FormParam("projectId") @ApiParam(required = true) long projectId,
                                      @FormParam("isOwner") @ApiParam(required = true) boolean isOwner) {

        try {
            var ctr = contributorDao.createContributor(userId, projectId, Boolean.valueOf(isOwner));
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
    @Override
    public Response updateContributor(@FormParam("ctrId") @ApiParam(required = true) long contributorId,
                                      @FormParam("isOwner") @ApiParam Boolean isOwner,
                                      @FormParam("isActive") @ApiParam Boolean isActive) {

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
    @ApiOperation(
            value = "Find Project Owner",
            notes = "Find a user who is a project owner",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project owner", response = Contributor.class),
            @ApiResponse(code = 204, message = "Project has no owner"),
            @ApiResponse(code = 404, message = "Project not found"),
            @ApiResponse(code = 406, message = "JWT token is wrong")})
    @Override
    public Response findProjectOwner(@QueryParam("projectId") @ApiParam(required = true) long projectId) {

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
    @Override
    public Response findProjectContributors(@QueryParam("projectId") @ApiParam(required = true) long projectId,
                                            @QueryParam("offset") @ApiParam(required = true) int offset,
                                            @QueryParam("limit") @ApiParam(required = true) int limit) {

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
