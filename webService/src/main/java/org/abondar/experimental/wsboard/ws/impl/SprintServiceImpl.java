package org.abondar.experimental.wsboard.ws.impl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.abondar.experimental.wsboard.dao.SprintDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Sprint;
import org.abondar.experimental.wsboard.ws.service.ProjectService;
import org.abondar.experimental.wsboard.ws.service.SprintService;
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
 * Sprint service implementation
 *
 * @author a.bondar
 */
@Path("/sprint")
public class SprintServiceImpl implements SprintService {

    private static Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    @Qualifier("sprintDao")
    private SprintDao sprintDao;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Create sprint",
            description = "Creates a new sprint by name start and end date",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Sprint created",
                            content = @Content(schema = @Schema(implementation = Sprint.class))),
                    @ApiResponse(responseCode = "205", description = "Sprint end date is wrong"),
                    @ApiResponse(responseCode = "206", description = "Form data is not complete or wrong"),
                    @ApiResponse(responseCode = "302", description = "Sprint with name already exists"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response createSprint(@FormParam("name") @Parameter(description = "Sprint name", required = true) String name,
                                 @FormParam("startDate") @Parameter(description = "Start date", required = true) String startDate,
                                 @FormParam("endDate") @Parameter(description = "End date", required = true) String endDate) {

        try {
            var startDt = convertDate(startDate);
            var endDt = convertDate(endDate);

            var sprint = sprintDao.createSprint(name, startDt, endDt);
            return Response.ok(sprint).build();

        } catch (DataCreationException ex) {
            logger.error(ex.getMessage());

            if (ex.getMessage().equals(LogMessageUtil.WRONG_END_DATE)) {
                return Response.status(Response.Status.RESET_CONTENT).entity(ex.getLocalizedMessage()).build();
            } else {
                return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
            }
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Update sprint",
            description = "Update existing sprint",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Sprint updated",
                            content = @Content(schema = @Schema(implementation = Sprint.class))),
                    @ApiResponse(responseCode = "204", description = "Form data is not complete or wrong"),
                    @ApiResponse(responseCode = "205", description = "Sprint end date is wrong"),
                    @ApiResponse(responseCode = "302", description = "Sprint with name already exists"),
                    @ApiResponse(responseCode = "404", description = "Sprint with id not exists"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response updateSprint(@FormParam("id") @Parameter(description = "Sprint id", required = true) long sprintId,
                                 @FormParam("name") @Parameter(description = "New sprint name") String name,
                                 @FormParam("startDate") @Parameter(description = "New start date") String startDate,
                                 @FormParam("endDate") @Parameter(description = "New end date") String endDate) {
        try {
            Date startDt = null;
            if (!startDate.isBlank()) {
                startDt = convertDate(startDate);

            }

            Date endDt = null;
            if (!endDate.isBlank()) {
                endDt = convertDate(endDate);
            }

            var sprint = sprintDao.updateSprint(sprintId, name, startDt, endDt);

            return Response.ok(sprint).build();
        } catch (DataCreationException ex) {
            logger.error(ex.getMessage());
            if (ex.getMessage().equals(LogMessageUtil.WRONG_END_DATE)) {
                return Response.status(Response.Status.RESET_CONTENT).entity(ex.getLocalizedMessage()).build();
            } else {
                return Response.status(Response.Status.NO_CONTENT).entity(ex.getLocalizedMessage()).build();
            }
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());

            if (ex.getMessage().equals(LogMessageUtil.SPRINT_NOT_EXISTS)) {
                return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
            } else {
                return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
            }
        }
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Find",
            description = "Find sprint by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Sprint with selected id",
                            content = @Content(schema = @Schema(implementation = Sprint.class))),
                    @ApiResponse(responseCode = "404", description = "Sprint with id not exists"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response getSprintById(@QueryParam("id") @Parameter(description = "Sprint id", required = true) long sprintId) {
        try {
            var sprint = sprintDao.getSprintById(sprintId);
            return Response.ok(sprint).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }

    @GET
    @Path("/find_all")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Find All",
            description = "Find all existing sprints",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of existing sprints",
                            content = @Content(schema = @Schema(implementation = Sprint.class))),
                    @ApiResponse(responseCode = "404", description = "No sprints are found"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response getSprints(@QueryParam("offset")
                               @Parameter(description = "Start of the range", required = true) int offset,
                               @QueryParam("limit")
                               @Parameter(description = "Number of sprints", required = true) int limit) {

        var sprints = sprintDao.getSprints(offset, limit);
        if (sprints.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(sprints).build();
    }

    @GET
    @Path("/delete")
    @Operation(
            summary = "Delete",
            description = "Delete Sprint by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Sprint is deleted"),
                    @ApiResponse(responseCode = "404", description = "Sprint with id not exists"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response deleteSprint(@QueryParam("id") @Parameter(description = "Sprint id", required = true) long sprintId) {
        try {
            sprintDao.deleteSprint(sprintId);
            return Response.ok().build();

        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }


}
