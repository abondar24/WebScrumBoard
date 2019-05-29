package org.abondar.experimental.wsboard.test.ws.impl;

import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.ws.service.ContributorService;

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
 * Test implementation of contributor web service
 */
@Path("/contributor")
public class ContributorServiceTestImpl implements ContributorService {

    private Contributor testContributor;
    private User testUser;
    private Project testProject;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response createContributor(@FormParam("userId") long userId,
                                      @FormParam("projectId") long projectId,
                                      @FormParam("isOwner") boolean isOwner) {

        if (testUser.getId() != userId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        if (testProject.getId() != projectId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        if (!testProject.isActive()) {
            return Response.status(Response.Status.MOVED_PERMANENTLY).entity(LogMessageUtil.PROJECT_NOT_ACTIVE).build();
        }

        if (isOwner) {
            if (testContributor != null && testContributor.isOwner()) {
                return Response.status(Response.Status.CONFLICT).entity(LogMessageUtil.PROJECT_HAS_OWNER).build();
            }
        }

        testContributor = new Contributor(userId, projectId, isOwner);

        return Response.ok(testContributor).build();
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateContributor(@FormParam("ctrId") long contributorId,
                                      @FormParam("isOwner") Boolean isOwner,
                                      @FormParam("isActive") Boolean isActive) {
        return null;
    }

    @GET
    @Path("/find_project_owner")
    @Override
    public Response findProjectOwner(@QueryParam("projectId") long projectId) {
        return null;
    }

    @GET
    @Path("/find_project_contributors")
    @Override
    public Response findProjectContributors(@QueryParam("projectId") long projectId,
                                            @QueryParam("offset") int offset,
                                            @QueryParam("limit") int limit) {
        return null;
    }


    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create_user")
    public Response createUser(@FormParam("login") String login,
                               @FormParam("email") String email,
                               @FormParam("firstName") String firstName,
                               @FormParam("lastName") String lastName,
                               @FormParam("password") String password,
                               @FormParam("roles") String roles) {


        testUser = new User(login, email, firstName, lastName, password, roles);
        testUser.setId(10);


        return Response.ok(testUser).build();
    }

    @POST
    @Path("/create_project")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProject(@FormParam("name") String name,
                                  @FormParam("startDate") String startDate) {


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
}
