package org.abondar.experimental.wsboard.ws.impl;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import java.util.Date;

/**
 * Project web service implementation
 *
 * @author a.bondar
 */
@Path("/project")
public class ProjectServiceImpl implements ProjectService {


    private static Logger logger = LoggerFactory.getLogger(ProjectService.class);
    @Autowired
    @Qualifier("projectDao")
    private ProjectDao projectDao;

    @POST
    @Path("/create")
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
            @ApiResponse(code = 302, message = "Project with name already exists"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    })
    @Override
    public Response createProject(@FormParam("name") @ApiParam(required = true) String name,
                                  @FormParam("startDate") @ApiParam(required = true) String startDate) {

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
            @ApiResponse(code = 404, message = "Project not found"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    })
    @Override
    public Response updateProject(@FormParam("id") @ApiParam(required = true) long id,
                                  @FormParam("name") @ApiParam String name,
                                  @FormParam("repo") @ApiParam String repo,
                                  @FormParam("isActive") @ApiParam Boolean isActive,
                                  @FormParam("endDate") @ApiParam String endDate) {

        try {
            Date endDt = null;
            if (endDate != null) {
                endDt = convertDate(endDate);
            }


            var prj = projectDao.updateProject(id, name, repo, isActive, endDt);
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


            switch (ex.getMessage()) {
                case LogMessageUtil.WRONG_END_DATE:
                    return Response.status(Response.Status.RESET_CONTENT).entity(ex.getLocalizedMessage()).build();
                case LogMessageUtil.PARSE_DATE_FAILED:
                    return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
                default:
                    return Response.status(Response.Status.MOVED_PERMANENTLY).entity(ex.getLocalizedMessage()).build();

            }

        }
    }

    @GET
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Delete",
            notes = "Delete project",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project deleted"),
            @ApiResponse(code = 404, message = "Project with id not exists"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    })
    @Override
    public Response deleteProject(@QueryParam("id") @ApiParam(required = true) long id) {
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
    @ApiOperation(
            value = "Find",
            notes = "Find project by id",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project found", response = Project.class),
            @ApiResponse(code = 404, message = "Project with id not exists"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    })
    @Override
    public Response findProjectById(@QueryParam("id") @ApiParam(required = true) long id) {
        try {
            var prj = projectDao.findProjectById(id);
            return Response.ok(prj).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }

    }


}
