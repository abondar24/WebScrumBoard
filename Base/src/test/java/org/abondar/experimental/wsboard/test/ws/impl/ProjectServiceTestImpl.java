package org.abondar.experimental.wsboard.test.ws.impl;

import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.ws.service.ProjectService;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Test implementation of project web service
 */
@Path("/project")
public class ProjectServiceTestImpl implements ProjectService {


    private Project testProject;
    private User testUser;


    @Override
    public Response createProject(String name, String startDate) {
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
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(LogMessageUtil.PARSE_DATE_FAILED).build();
        }

        testProject = new Project(name, stDate);
        testProject.setId(10);

        return Response.ok(testProject).build();
    }

    @Override
    public Response updateProject(long id, String name, String repo, Boolean isActive,
                                  String endDate,String description) {
        if (testProject.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        if (testProject.getName().equals(name)) {
            return Response.status(Response.Status.FOUND).entity(LogMessageUtil.PROJECT_EXISTS).build();
        }


        if (name != null && !name.isBlank()) {
            testProject.setName(name);
        }

        testProject.setRepository(repo);
        testProject.setDescription(description);

        if (isActive != null) {

            if (!isActive) {
                Date endDt;
                try {
                    endDt = convertDate(endDate);
                    System.out.println(endDt);
                    if (testProject.getStartDate().after(endDt)) {
                        return Response.status(Response.Status.RESET_CONTENT).entity(LogMessageUtil.WRONG_END_DATE).build();
                    }
                    testProject.setEndDate(endDt);

                } catch (DataCreationException ex) {
                    return Response.status(Response.Status.PARTIAL_CONTENT).entity(LogMessageUtil.PARSE_DATE_FAILED).build();
                }

            }

            if (!testProject.isActive() && isActive) {
                return Response.status(Response.Status.MOVED_PERMANENTLY).entity(LogMessageUtil.PROJECT_CANNOT_BE_REACTIVATED).build();
            }

            testProject.setActive(isActive);

        }

        return Response.ok(testProject).build();
    }

    @Override
    public Response deleteProject(long id) {
        if (testProject.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        return Response.ok().build();
    }

    @Override
    public Response findProjectById(long id) {
        if (testProject.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        return Response.ok(testProject).build();
    }

    @Override
    public Response findUserProjects(long userId) {
        if (userId!=testUser.getId()){
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        return Response.ok(List.of(testProject,new Project())).build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/user")
    public Response createUser() {


        testUser = new User("test", "test", "test", "testy", "test", "test");
        testUser.setId(10);


        return Response.ok(testUser).build();
    }

    private Date convertDate(String strDate) throws DataCreationException {
        var format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            return format.parse(strDate);
        } catch (ParseException ex) {
            throw new DataCreationException(LogMessageUtil.PARSE_DATE_FAILED);
        }

    }


}
