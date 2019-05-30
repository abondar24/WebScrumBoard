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
import java.util.List;
import java.util.stream.Collectors;

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
                                      @FormParam("isOwner") String isOwner) {

        var owner = new Contributor(5, projectId, true);
        if (testUser.getId() != userId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        if (testProject.getId() != projectId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        if (!testProject.isActive()) {
            return Response.status(Response.Status.MOVED_PERMANENTLY).entity(LogMessageUtil.PROJECT_NOT_ACTIVE).build();
        }

        var isOwnerVal = Boolean.parseBoolean(isOwner);
        if (isOwnerVal && owner.isOwner()) {
            return Response.status(Response.Status.CONFLICT).entity(LogMessageUtil.PROJECT_HAS_OWNER).build();

        }

        testContributor = new Contributor(userId, projectId, isOwnerVal);
        testContributor.setId(10);

        return Response.ok(testContributor).build();
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateContributor(@FormParam("ctrId") long contributorId,
                                      @FormParam("isOwner") String isOwner,
                                      @FormParam("isActive") String isActive) {

        if (testContributor.getId() != contributorId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS).build();
        }

        boolean isOwnerVal;
        if (isOwner != null && !isOwner.isBlank()) {
            isOwnerVal = Boolean.parseBoolean(isOwner);

            if (isOwnerVal) {
                if (testContributor.isOwner()) {
                    return Response.status(Response.Status.FOUND).entity(LogMessageUtil.PROJECT_HAS_OWNER).build();
                }

                if (!testContributor.isActive()) {
                    return Response.status(Response.Status.GONE).entity(LogMessageUtil.CONTRIBUTOR_NOT_ACTIVE).build();
                }

            } else if (!testContributor.isOwner()) {
                return Response.status(Response.Status.CONFLICT).entity(LogMessageUtil.PROJECT_HAS_NO_OWNER).build();
            }
            testContributor.setOwner(isOwnerVal);
        }


        boolean isActiveVal;
        if (isActive != null && !isActive.isBlank()) {
            isActiveVal = Boolean.parseBoolean(isActive);
            if (testContributor.isOwner() && !isActiveVal) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(LogMessageUtil.CONTRIBUTOR_CANNOT_BE_DEACTIVATED).build();
            }
            testContributor.setActive(isActiveVal);
        }


        return Response.ok(testContributor).build();
    }

    @GET
    @Path("/find_project_owner")
    @Override
    public Response findProjectOwner(@QueryParam("projectId") long projectId) {


        if (testProject.getId() != projectId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        if (!testContributor.isOwner()) {
            return Response.status(Response.Status.NO_CONTENT).entity(LogMessageUtil.PROJECT_HAS_NO_OWNER).build();
        }

        return Response.ok(testContributor).build();
    }

    @GET
    @Path("/find_project_contributors")
    @Override
    public Response findProjectContributors(@QueryParam("projectId") long projectId,
                                            @QueryParam("offset") int offset,
                                            @QueryParam("limit") int limit) {

        if (testProject.getId() != projectId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }


        var contributors = List.of(testContributor,
                new Contributor(1, testProject.getId(), false),
                new Contributor(2, testProject.getId(), false),
                new Contributor(3, testProject.getId(), false),
                new Contributor(4, testProject.getId(), false));

        if (offset == -1) {
            return Response.ok(contributors).build();
        }

        contributors = contributors.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        if (contributors.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(contributors).build();


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


    @POST
    @Path("/update_project")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProject(@FormParam("isActive") String isActive) {

        testProject.setActive(Boolean.valueOf(isActive));

        return Response.ok().build();
    }
}
