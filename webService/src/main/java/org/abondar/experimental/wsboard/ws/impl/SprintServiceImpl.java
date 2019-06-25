package org.abondar.experimental.wsboard.ws.impl;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.abondar.experimental.wsboard.dao.SprintDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Sprint;
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
@Api("Sprint api")
public class SprintServiceImpl implements SprintService {

    private static Logger logger = LoggerFactory.getLogger(SprintService.class);

    @Autowired
    @Qualifier("sprintDao")
    private SprintDao sprintDao;

    @POST
    @Path("/create")
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
            @ApiResponse(code = 302, message = "Sprint with name already exists"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    })
    @Override
    public Response createSprint(@FormParam("name") @ApiParam(required = true) String name,
                                 @FormParam("startDate") @ApiParam(required = true) String startDate,
                                 @FormParam("endDate") @ApiParam(required = true) String endDate) {

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
    @ApiOperation(
            value = "Update sprint",
            notes = "Update sprint name,start and end date",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sprint updated", response = Sprint.class),
            @ApiResponse(code = 204, message = "Form data is not complete or wrong"),
            @ApiResponse(code = 205, message = "Sprint end date is wrong"),
            @ApiResponse(code = 302, message = "Sprint with name already exists"),
            @ApiResponse(code = 404, message = "Sprint with id not exists"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    })
    @Override
    public Response updateSprint(@FormParam("id") @ApiParam(required = true) long sprintId,
                                 @FormParam("name") @ApiParam String name,
                                 @FormParam("startDate") @ApiParam String startDate,
                                 @FormParam("endDate") @ApiParam String endDate) {
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
    @ApiOperation(
            value = "Find",
            notes = "Find sprint by id",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sprint with selected id", response = Sprint.class),
            @ApiResponse(code = 404, message = "Sprint with id not exists"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    })
    @Override
    public Response getSprintById(@QueryParam("id") @ApiParam(required = true) long sprintId) {
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
    @ApiOperation(
            value = "Find All",
            notes = "Find all existing sprints",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of existing sprints", response = Sprint.class),
            @ApiResponse(code = 404, message = "No sprints are found"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    }
    )
    @Override
    public Response getSprints(@QueryParam("offset") @ApiParam(required = true) int offset,
                               @QueryParam("limit") @ApiParam(required = true) int limit) {

        var sprints = sprintDao.getSprints(offset, limit);
        if (sprints.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(sprints).build();
    }

    @GET
    @Path("/delete")
    @ApiOperation(
            value = "Delete",
            notes = "Delete Sprint by id",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sprint is deleted"),
            @ApiResponse(code = 404, message = "Sprint with id not exists"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    }
    )
    @Override
    public Response deleteSprint(@QueryParam("id") @ApiParam(required = true) long sprintId) {
        try {
            sprintDao.deleteSprint(sprintId);
            return Response.ok().build();

        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }


}
