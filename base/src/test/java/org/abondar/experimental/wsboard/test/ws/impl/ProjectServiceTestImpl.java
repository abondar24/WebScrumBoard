package org.abondar.experimental.wsboard.test.ws.impl;

import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.ws.service.ProjectService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

import static org.abondar.experimental.wsboard.dao.data.LogMessageUtil.BLANK_DATA;
import static org.abondar.experimental.wsboard.dao.data.LogMessageUtil.PROJECT_CANNOT_BE_REACTIVATED;
import static org.abondar.experimental.wsboard.dao.data.LogMessageUtil.PROJECT_EXISTS;
import static org.abondar.experimental.wsboard.dao.data.LogMessageUtil.PROJECT_NOT_EXISTS;
import static org.abondar.experimental.wsboard.dao.data.LogMessageUtil.PROJECT_WRONG_END_DATE;

/**
 * Test implementation of project web service
 */
@Path("/project")
public class ProjectServiceTestImpl implements ProjectService {

    private Project testProject;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response createProject(String name, Date startDate) {
        var existingProject = new Project("exists", new Date());

        if (existingProject.getName().equals(name)) {
            return Response.status(Response.Status.FOUND).entity(PROJECT_EXISTS).build();
        }

        if (name == null || name.isBlank() || startDate == null) {
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(BLANK_DATA).build();
        }

        testProject = new Project(name, startDate);

        return Response.ok(testProject).build();
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateProject(Long id, String name, String repo, Boolean isActive, Date endDate) {
        if (testProject.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(PROJECT_NOT_EXISTS).build();
        }

        if (testProject.getStartDate().after(endDate) || (!isActive && endDate == null)) {
            return Response.status(Response.Status.RESET_CONTENT).entity(PROJECT_WRONG_END_DATE).build();
        }

        if (!testProject.isActive() && isActive) {
            return Response.status(Response.Status.MOVED_PERMANENTLY).entity(PROJECT_CANNOT_BE_REACTIVATED).build();
        }

        testProject.setName(name);
        testProject.setRepository(repo);
        testProject.setActive(isActive);
        testProject.setEndDate(endDate);

        return null;
    }

    @GET
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response deleteProject(Long id) {
        if (testProject.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(PROJECT_NOT_EXISTS).build();
        }

        return Response.ok().build();
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findProjectById(long id) {
        if (testProject.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(PROJECT_NOT_EXISTS).build();
        }

        return Response.ok(testProject).build();
    }
}
