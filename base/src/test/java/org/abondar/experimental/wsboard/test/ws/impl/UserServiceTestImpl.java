package org.abondar.experimental.wsboard.test.ws.impl;

import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.CannotPerformOperationException;
import org.abondar.experimental.wsboard.dao.exception.InvalidHashException;
import org.abondar.experimental.wsboard.dao.password.PasswordUtil;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.SecurityCode;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.ws.service.AuthService;
import org.abondar.experimental.wsboard.ws.service.UserService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * Test implementation of user web service
 */

public class UserServiceTestImpl implements UserService {

    private AuthService authService;

    private User testUser;
    private Contributor testContributor;
    private SecurityCode testCode;

    public UserServiceTestImpl(AuthService authService) {
        this.authService = authService;

    }

    public Response createUser(String login, String email, String firstName,
                               String lastName, String password, String roles) {

        var existingUser = new User();
        existingUser.setLogin("testLogin");

        if (existingUser.getLogin().equals(login)) {
            return Response.status(Response.Status.FOUND).entity(LogMessageUtil.USER_EXISTS).build();
        }

        if ((login == null || login.isBlank()) || (password == null || password.isBlank())
                || (email == null || email.isBlank()) || (firstName == null || firstName.isBlank())
                || (lastName == null || lastName.isBlank()) || (roles == null || roles.isEmpty())) {
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(LogMessageUtil.BLANK_DATA).build();
        }

        String[] rolesArr = roles.split(";");

        if (rolesArr.length == 0 || !roles.contains(";")) {
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(LogMessageUtil.USER_NO_ROLES).build();
        }

        String pwdHash;
        try {
            pwdHash = PasswordUtil.createHash(password);

        } catch (CannotPerformOperationException ex) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(ex.getLocalizedMessage()).build();
        }


        testUser = new User(login, email, firstName, lastName, pwdHash, roles);
        testUser.setId(10);

        testCode = new SecurityCode(12345, testUser.getId());


        return Response.ok(testUser).build();


    }

    @Override
    public Response updateUser(long id, String firstName, String lastName, String email, String roles) {
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

    @Override
    public Response updateAvatar(long id, byte[] avatar) {
        if (testUser.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        testUser.setAvatar(avatar);

        return Response.ok(testUser).build();
    }

    @Override
    public Response updateLogin(String login, long id) {
        if (testUser.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        if (testUser.getLogin().equals(login)) {
            return Response.status(Response.Status.FOUND).entity(LogMessageUtil.USER_EXISTS).build();
        }

        testUser.setLogin(login);
        return Response.ok(testUser).build();
    }

    @Override
    public Response updatePassword(String oldPassword, String newPassword, long id) {

        if (testUser.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        try {
            if (PasswordUtil.verifyPassword(oldPassword, testUser.getPassword())) {
                testUser.setPassword(PasswordUtil.createHash(newPassword));
                return Response.ok(testUser).build();
            }
        } catch (CannotPerformOperationException ex) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(ex.getLocalizedMessage()).build();
        } catch (InvalidHashException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getLocalizedMessage()).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).entity(LogMessageUtil.USER_UNAUTHORIZED).build();
    }

    @Override
    public Response deleteUser(long id) {
        if (testUser.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        if (testContributor != null && testContributor.isOwner()) {
            testContributor = null;
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(LogMessageUtil.USER_IS_PROJECT_OWNER).build();
        }

        testUser.setDeleted();
        return Response.ok(testUser).build();
    }

    @Override
    public Response loginUser(String login, String password) {

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

        return Response.status(Response.Status.UNAUTHORIZED).entity(LogMessageUtil.USER_UNAUTHORIZED).build();
    }

    @Override
    public Response logoutUser(long id) {
        if (testUser.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        return Response.ok().cookie(new NewCookie(new Cookie("X-JWT-AUTH", "", "/", ""),
                "JWT token", 24000, false)).build();
    }

    @Override
    public Response findUserByLogin(String login) {
        if (!testUser.getLogin().equals(login)) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        return Response.ok(testUser).build();
    }

    @Override
    public Response resetPassword(long id) {
        if (testUser.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        testUser.setPassword("reset");
        return Response.ok().build();
    }

    @Override
    public Response enterCode(long userId) {
        if (testUser.getId() != userId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        return Response.ok().build();
    }

    @GET
    @Path("create_test_contributor")
    public Response createTestContributor(@QueryParam("id") long userId) {
        this.testContributor = new Contributor(userId, 7, true);

        return Response.ok().build();
    }

    private NewCookie createCookie(String login) {
        return new NewCookie(new Cookie("X-JWT-AUTH",
                authService.createToken(login, "test", null), "/", null),
                "JWT token", 6000, new Date((new Date()).getTime() + 60000), false, true);

    }
}
