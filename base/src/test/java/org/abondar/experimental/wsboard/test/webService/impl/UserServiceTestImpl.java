package org.abondar.experimental.wsboard.test.webService.impl;

import org.abondar.experimental.wsboard.dao.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.CannotPerformOperationException;
import org.abondar.experimental.wsboard.dao.password.PasswordUtil;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.webService.service.AuthService;
import org.abondar.experimental.wsboard.webService.service.UserService;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Date;

public class UserServiceTestImpl implements UserService {

    private AuthService authService;

    private User user;

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

        var testUser = new User();
        testUser.setLogin("testLogin");

        if (testUser.getLogin().equals(login)) {
            return Response.status(Response.Status.FOUND).entity(ErrorMessageUtil.USER_EXISTS).build();
        }

        if ((login == null || login.isBlank()) || (password == null || password.isBlank())
                || (email == null || email.isBlank()) || (firstName == null || firstName.isBlank())
                || (lastName == null || lastName.isBlank()) || (roles == null || roles.isEmpty())) {
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(ErrorMessageUtil.BLANK_DATA).build();
        }

        String[] rolesArr = roles.split(";");

        if (rolesArr.length == 0 || !roles.contains(";")) {
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(ErrorMessageUtil.USER_NO_ROLES).build();
        }

        String pwdHash;
        try {
            pwdHash = PasswordUtil.createHash(password);

        } catch (CannotPerformOperationException ex) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(ex.getLocalizedMessage()).build();
        }


        user = new User(login, email, firstName, lastName, pwdHash, roles);
        user.setId(10);


        return Response.ok(user).cookie(createCookie(user.getLogin())).build();


    }

    @Override
    public Response updateUser(long id, String firstName, String lastName, String email, String roles) {
        return null;
    }

    @Override
    public Response updateAvatar(long id, byte[] avatar) {
        return null;
    }

    @Override
    public Response updateLogin(String login, long id) {
        return null;
    }

    @Override
    public Response updatePassword(String oldPassword, String newPassword, long id) {
        return null;
    }

    @Override
    public Response deleteUser(long id) {
        return null;
    }

    @Override
    public Response loginUser(String login, String password) {
        return null;
    }

    @Override
    public Response logoutUser(long id) {
        return null;
    }

    private NewCookie createCookie(String login) {
        return new NewCookie(new Cookie("X-JWT-AUTH",
                authService.createToken(login, "test", null), "/", null),
                "JWT token", 6000, new Date((new Date()).getTime() + 60000), false, false);

    }
}
