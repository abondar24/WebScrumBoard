package org.abondar.experimental.wsboard.test.ws.impl;

import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.ws.service.ProjectService;

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
 * Test implementation of project web service
 */
@Path("/project")
public class ProjectServiceTestImpl implements ProjectService {

    private static final String DATE_FORMAT = "dd/MM/yyyy";


    private Project testProject;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response createProject(@FormParam("name") String name,
                                  @FormParam("startDate") String startDate) {
        var existingProject = new Project("exists", new Date());

        if (existingProject.getName().equals(name)) {
            return Response.status(Response.Status.FOUND).entity(LogMessageUtil.PROJECT_EXISTS).build();
        }

        if (name == null || name.isBlank() || startDate == null) {
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(LogMessageUtil.BLANK_DATA).build();
        }

        Date stDate;
        try {
            stDate = convertDate(startDate);
        } catch (DataCreationException ex) {
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(LogMessageUtil.PROJECT_PARSE_DATE_FAILED).build();
        }

        testProject = new Project(name, stDate);
        testProject.setId(10);

        return Response.ok(testProject).build();
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateProject(@FormParam("id") long id,
                                  @FormParam("name") String name,
                                  @FormParam("repo") String repo,
                                  @FormParam("isActive") Boolean isActive,
                                  @FormParam("endDate") String endDate) {
        if (testProject.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        Date endDt;
        try {
            endDt = convertDate(endDate);
        } catch (DataCreationException ex) {
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(LogMessageUtil.PROJECT_PARSE_DATE_FAILED).build();
        }

        if (testProject.getStartDate().after(endDt) || (endDt == null)) {
            return Response.status(Response.Status.RESET_CONTENT).entity(LogMessageUtil.PROJECT_WRONG_END_DATE).build();
        }

        if (!testProject.isActive() && isActive) {
            return Response.status(Response.Status.MOVED_PERMANENTLY).entity(LogMessageUtil.PROJECT_CANNOT_BE_REACTIVATED).build();
        }

        testProject.setName(name);
        testProject.setRepository(repo);
        testProject.setActive(isActive);
        testProject.setEndDate(endDt);

        return null;
    }

    @GET
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response deleteProject(@QueryParam("id") long id) {
        if (testProject.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        return Response.ok().build();
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findProjectById(@QueryParam("id") long id) {
        if (testProject.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        return Response.ok(testProject).build();
    }

    private Date convertDate(String strDate) throws DataCreationException {
        var format = new SimpleDateFormat(DATE_FORMAT);

        try {
            return format.parse(strDate);
        } catch (ParseException ex) {
            throw new DataCreationException(LogMessageUtil.PROJECT_WRONG_END_DATE);
        }

    }
}
