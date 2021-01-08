package org.abondar.experimental.wsboard.service.impl;

import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.user.user.User;
import org.abondar.experimental.wsboard.ws.service.ContributorService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test implementation of contributor web service
 *
 * @author a.bondar
 */
@Path("/contributor")
public class ContributorServiceTestImpl implements ContributorService {

    private Contributor testContributor;
    private User testUser;
    private Project testProject;

    @Override
    public Response createContributor(long userId, long projectId, boolean isOwner) {

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
            if (testContributor != null && testContributor.isOwner())
            return Response.status(Response.Status.CONFLICT).entity(LogMessageUtil.CONTRIBUTOR_IS_ALREADY_OWNER).build();

        }

        if (testContributor != null && testProject.getId()==projectId && testUser.getId()==userId){
            return Response.status(Response.Status.FOUND).entity(LogMessageUtil.CONTRIBUTOR_EXISTS_LOG).build();
        }


            testContributor = new Contributor(userId, projectId, isOwner);
        testContributor.setId(10);

        return Response.ok(testContributor).build();
    }

    @Override
    public Response updateContributor(long userId,long projectId, Boolean isOwner, Boolean isActive) {

        if (testProject.getId() != projectId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        if (testUser.getId() != userId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        if (testContributor==null) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS).build();
        }

        if (isOwner != null) {

            if (isOwner) {
                if (testContributor.isOwner()) {
                    return Response.status(Response.Status.FOUND).entity(LogMessageUtil.CONTRIBUTOR_IS_ALREADY_OWNER).build();
                }

                if (!testContributor.isActive()) {
                    return Response.status(Response.Status.GONE).entity(LogMessageUtil.CONTRIBUTOR_NOT_ACTIVE).build();
                }

            } else if (!testContributor.isOwner()) {
                return Response.status(Response.Status.CONFLICT).entity(LogMessageUtil.PROJECT_HAS_NO_OWNER).build();
            }
            testContributor.setOwner(isOwner);
        }


        if (isActive != null) {
            if (testContributor.isOwner() && !isActive) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(LogMessageUtil.CONTRIBUTOR_CANNOT_BE_DEACTIVATED).build();
            }
            testContributor.setActive(isActive);
        }


        return Response.ok(testContributor).build();
    }

    @Override
    public Response findProjectOwner(long projectId) {


        if (testProject.getId() != projectId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        if (!testContributor.isOwner()) {
            return Response.status(Response.Status.NO_CONTENT).entity(LogMessageUtil.PROJECT_HAS_NO_OWNER).build();
        }

        return Response.ok(testContributor).build();
    }

    @Override
    public Response findProjectContributors(long projectId, int offset, int limit) {

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
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.ok(contributors).build();


    }

    @Override
    public Response countProjectContributors(long projectId) {

        return Response.ok(7).build();
    }

    @Override
    public Response findContributorsByUserId(long userId, int offset, int limit) {
        if (testUser.getId()!=userId){
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
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
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.ok(contributors).build();

    }

    @Override
    public Response findProjectContributor(long userId, long projectId) {
        if (testUser.getId()!=userId){
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        if (testProject.getId()!=projectId){
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        return Response.ok(testContributor.getId()).build();
    }

    @Override
    public Response findContributorByLogin(long projectId, String login) {
        if (testProject.getId()!=projectId){
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        if (!testUser.getLogin().equals(login)){
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS).build();
        }

        return Response.ok(testContributor).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/user")
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
    @Path("/project")
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


    @PUT
    @Path("/project")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProject(@FormParam("isActive") boolean isActive) {

        testProject.setActive(isActive);

        return Response.ok().build();
    }


    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete() {
        testContributor = null;
        return Response.ok().build();
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
