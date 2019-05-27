package org.abondar.experimental.wsboard.ws.impl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.abondar.experimental.wsboard.dao.ProjectDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.ws.service.ProjectService;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Project CRUD and find service
 */
@Path("/project")
public class ProjectServiceImpl implements ProjectService {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private static Logger logger = LoggerFactory.getLogger(ProjectService.class);
    @Autowired
    @Qualifier("projectDao")
    private ProjectDao projectDao;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Create project",
            description = "Creates a new project by name and start date",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project created",
                            content = @Content(schema = @Schema(implementation = Project.class))),
                    @ApiResponse(responseCode = "206", description = "Form data is not complete"),
                    @ApiResponse(responseCode = "302", description = "Project with name already exists"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response createProject(@FormParam("name")
                                  @Parameter(description = "Project name", required = true) String name,
                                  @FormParam("startDate")
                                  @Parameter(description = "Project start date", required = true) String startDate) {

        try {
            var prj = projectDao.createProject(name, convertDate(startDate));

            return Response.ok(prj).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
        }
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Update project",
            description = "Update project name,repository,activity,end date",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project updated",
                            content = @Content(schema = @Schema(implementation = Project.class))),
                    @ApiResponse(responseCode = "205", description = "Wrong end date for project"),
                    @ApiResponse(responseCode = "206", description = "End date can't be parsed"),
                    @ApiResponse(responseCode = "301", description = "Project can't be reactivated"),
                    @ApiResponse(responseCode = "302", description = "Project with name already exists"),
                    @ApiResponse(responseCode = "404", description = "Project not found"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response updateProject(@FormParam("id")
                                  @Parameter(description = "Project ID", required = true) long id,
                                  @FormParam("name")
                                  @Parameter(description = "Project name") String name,
                                  @FormParam("repo")
                                  @Parameter(description = "Project repository")
                                          String repo,
                                  @FormParam("isActive")
                                  @Parameter(description = "Project status") String isActive,
                                  @FormParam("endDate")
                                  @Parameter(description = "Project end date(required if status is false)")
                                          String endDate) {

        try {
            Date endDt = null;
            if (endDate != null) {
                endDt = convertDate(endDate);
            }

            Boolean isActiveVal = null;
            if (isActive != null) {
                isActiveVal = Boolean.valueOf(isActive);
            }

            var prj = projectDao.updateProject(id, name, repo, isActiveVal, endDt);
            return Response.ok(prj).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            if (ex.getMessage().equals(LogMessageUtil.PROJECT_NOT_EXISTS)) {
                return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
            } else {
                return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
            }

        } catch (DataCreationException ex) {
            logger.error(ex.getMessage());

            if (ex.getMessage().equals(LogMessageUtil.PROJECT_WRONG_END_DATE)) {
                return Response.status(Response.Status.RESET_CONTENT).entity(ex.getLocalizedMessage()).build();
            } else if (ex.getMessage().equals(LogMessageUtil.PROJECT_PARSE_DATE_FAILED)) {
                return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
            } else {
                return Response.status(Response.Status.MOVED_PERMANENTLY).entity(ex.getLocalizedMessage()).build();
            }

        }
    }

    @GET
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Delete",
            description = "Delete project",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Delete project"),
                    @ApiResponse(responseCode = "404", description = "Project with id not exists"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response deleteProject(@QueryParam("id") @Parameter(description = "Project ID", required = true) long id) {
        try {
            projectDao.deleteProject(id);
            return Response.ok().build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Find",
            description = "Find project by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project found",
                            content = @Content(schema = @Schema(implementation = Project.class))),
                    @ApiResponse(responseCode = "404", description = "Project with id not exists"),
                    @ApiResponse(responseCode = "406", description = "JWT token is wrong")
            }
    )
    @Override
    public Response findProjectById(@QueryParam("id") @Parameter(description = "Project ID", required = true) long id) {
        try {
            var prj = projectDao.findProjectById(id);
            return Response.ok(prj).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }

    }

    private Date convertDate(String strDate) throws DataCreationException {
        var format = new SimpleDateFormat(DATE_FORMAT);

        try {
            return format.parse(strDate);
        } catch (ParseException ex) {
            logger.error(ex.getMessage());
            throw new DataCreationException(LogMessageUtil.PROJECT_WRONG_END_DATE);
        }

    }
}
