package org.abondar.experimental.wsboard.test.ws.impl;

import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.CannotPerformOperationException;
import org.abondar.experimental.wsboard.dao.exception.InvalidHashException;
import org.abondar.experimental.wsboard.dao.password.PasswordUtil;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.ws.service.AuthService;
import org.abondar.experimental.wsboard.ws.service.UserService;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * Test implementation of user web service
 */
@Path("/user")
public class UserServiceTestImpl implements UserService {

    private AuthService authService;

    private User testUser;
    private Contributor testContributor;

    public UserServiceTestImpl(AuthService authService) {
        this.authService = authService;

    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    @PermitAll
    @Override
    public Response createUser(@FormParam("login") String login,
                               @FormParam("email") String email,
                               @FormParam("firstName") String firstName,
                               @FormParam("lastName") String lastName,
                               @FormParam("password") String password,
                               @FormParam("roles") String roles) {

        var existingUser = new User();
        existingUser.setLogin("testLogin");

        if (existingUser.getLogin().equals(login)) {
            return Response.status(Response.Status.FOUND).entity(LogMessageUtil.USER_EXISTS).build();
        }

        if ((login == null || login.isBlank()) || (password == null || password.isBlank())
                || (email == null || email.isBlank()) || (firstName == null || firstName.isBlank())
                || (lastName == null || lastName.isBlank()) || (roles == null || roles.isEmpty())) {
            return Response.status(Response.Status.NO_CONTENT).entity(LogMessageUtil.BLANK_DATA).build();
        }

        String[] rolesArr = roles.split(";");

        if (rolesArr.length == 0 || !roles.contains(";")) {
            return Response.status(Response.Status.NO_CONTENT).entity(LogMessageUtil.USER_NO_ROLES).build();
        }

        String pwdHash;
        try {
            pwdHash = PasswordUtil.createHash(password);

        } catch (CannotPerformOperationException ex) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(ex.getLocalizedMessage()).build();
        }


        this.testUser = new User(login, email, firstName, lastName, pwdHash, roles);
        this.testUser.setId(10);


        return Response.ok(this.testUser).cookie(createCookie(this.testUser.getLogin())).build();


    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    @Override
    public Response updateUser(@FormParam("id") long id,
                               @FormParam("firstName") String firstName,
                               @FormParam("lastName") String lastName,
                               @FormParam("email") String email,
                               @FormParam("roles") String roles) {
        if (testUser.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        if (roles != null) {
            String[] rolesArr = roles.split(";");

            if (rolesArr.length == 0 || !roles.contains(";")) {
                return Response.status(Response.Status.NO_CONTENT).entity(LogMessageUtil.USER_NO_ROLES).build();
            }
        }

        testUser.setFirstName(firstName);
        testUser.setLastName(lastName);
        testUser.setEmail(email);
        testUser.setRoles(roles);

        return Response.ok(testUser).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update_avatar")
    @Override
    public Response updateAvatar(long id, byte[] avatar) {
        if (testUser.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }


        if (avatar != null && avatar.length == 0) {
            return Response.status(Response.Status.NO_CONTENT).entity(LogMessageUtil.USER_AVATAR_EMPTY).build();
        }

        testUser.setAvatar(avatar);

        return Response.ok(testUser).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update_login")
    @Override
    public Response updateLogin(@FormParam("login") String login, @FormParam("id") long id) {
        if (testUser.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        if (testUser.getLogin().equals(login)) {
            return Response.status(Response.Status.FOUND).entity(LogMessageUtil.USER_EXISTS).build();
        }

        testUser.setLogin(login);
        return Response.ok(testUser).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update_password")
    @Override
    public Response updatePassword(@FormParam("oldPassword") String oldPassword,
                                   @FormParam("newPassword") String newPassword,
                                   @FormParam("id") long id) {

        if (testUser.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        try {
            if (PasswordUtil.verifyPassword(oldPassword, testUser.getPassword())) {
                testUser.setPassword(PasswordUtil.createHash(newPassword));
            }
            return Response.ok(testUser).build();
        } catch (CannotPerformOperationException ex) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(ex.getLocalizedMessage()).build();
        } catch (InvalidHashException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getLocalizedMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete")
    @Override
    public Response deleteUser(@QueryParam("id") long id) {
        if (testUser.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        if (testContributor != null && testContributor.isOwner()) {
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(LogMessageUtil.USER_IS_PROJECT_OWNER).build();
        }

        testUser.setDeleted();
        return Response.ok(testUser).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login")
    @PermitAll
    @Override
    public Response loginUser(@FormParam("login") String login,
                              @FormParam("password") String password) {

        if (!testUser.getLogin().equals(login)) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        try {
            if (PasswordUtil.verifyPassword(password, testUser.getPassword())) {
                return Response.ok().cookie(createCookie(login)).build();
            }
        } catch (CannotPerformOperationException ex) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(ex.getLocalizedMessage()).build();
        } catch (InvalidHashException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getLocalizedMessage()).build();
        }

        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/logout")
    @Override
    public Response logoutUser(@QueryParam("id") long id) {
        if (testUser.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        return Response.ok().cookie(new NewCookie(new Cookie("X-JWT-AUTH", "", "/", ""),
                "JWT token", 24000, false)).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("createTestContributor")
    public Response createTestContributor(@FormParam("userId") long userId,
                                          @FormParam("projectId") long projectId,
                                          @FormParam("isOwner") boolean isOwner) {
        this.testContributor = new Contributor(userId, projectId, isOwner);

        return Response.ok().build();
    }

    private NewCookie createCookie(String login) {
        return new NewCookie(new Cookie("X-JWT-AUTH",
                authService.createToken(login, "test", null), "/", null),
                "JWT token", 6000, new Date((new Date()).getTime() + 60000), false, true);

    }
}
