package org.abondar.experimental.wsboard.webService.impl;

import org.abondar.experimental.wsboard.dao.UserDao;
import org.abondar.experimental.wsboard.dao.exception.CannotPerformOperationException;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.dao.exception.InvalidHashException;
import org.abondar.experimental.wsboard.datamodel.User;
import org.abondar.experimental.wsboard.webService.service.AuthService;
import org.abondar.experimental.wsboard.webService.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

/**
 * User crud and login webservice
 */
@Path("/user")
public class UserServiceImpl implements UserService {

    private static final String COOKIE_ISSUER = "borscht sysyems";
    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    @Qualifier("userDao")
    private UserDao dao;

    private AuthService authService;

    public UserServiceImpl(AuthService authService) {
        this.authService = authService;
    }


    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    @PermitAll
    @Override
    public Response createUser(String login, String email, String firstName, String lastName,
                               String password, List<String> roles) {

        try {
            User user = dao.createUser(login, password, email, firstName, lastName, roles);

            return Response.ok(user).cookie(createCookie(user.getLogin())).build();
        } catch (CannotPerformOperationException ex) {
            logger.error(ex.getLocalizedMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(ex.getLocalizedMessage()).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getLocalizedMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ex) {
            logger.error(ex.getLocalizedMessage());
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(ex.getLocalizedMessage()).build();
        }

    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    @Override
    public Response updateUser(long id, String firstName, String lastName, String email,
                               List<String> roles, byte[] avatar) {
        try {
            User user = dao.updateUser(id, firstName, lastName, email, roles, avatar);

            return Response.ok(user).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getLocalizedMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ex) {
            logger.error(ex.getLocalizedMessage());
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(ex.getLocalizedMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update_login")
    @Override
    public Response updateLogin(String login, long id) {
        try {
            User user = dao.updateLogin(login, id);

            return Response.ok(user).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getLocalizedMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ex) {
            logger.error(ex.getLocalizedMessage());
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(ex.getLocalizedMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update_password")
    @Override
    public Response updatePassword(String oldPassword, String newPassword, long id) {
        try {
            User user = dao.updatePassword(oldPassword, newPassword, id);

            return Response.ok(user).build();
        } catch (InvalidHashException ex) {
            logger.error(ex.getLocalizedMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getLocalizedMessage()).build();
        } catch (CannotPerformOperationException ex) {
            logger.error(ex.getLocalizedMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(ex.getLocalizedMessage()).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getLocalizedMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete")
    @Override
    public Response deleteUser(@QueryParam("id") long id) {
        try {
            User user = dao.deleteUser(id);

            return Response.ok(user).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getLocalizedMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ex) {
            logger.error(ex.getLocalizedMessage());
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(ex.getLocalizedMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login")
    @Override
    public Response loginUser(String login, String password) {
        try {
            dao.loginUser(login, password);

            return Response.ok().cookie(createCookie(login)).build();
        } catch (InvalidHashException ex) {
            logger.error(ex.getLocalizedMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getLocalizedMessage()).build();
        } catch (CannotPerformOperationException ex) {
            logger.error(ex.getLocalizedMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(ex.getLocalizedMessage()).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getLocalizedMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/logout")
    @Override
    public Response logoutUser(@QueryParam("id") long id) {
        try {
            dao.logoutUser(id);

            return Response.ok().build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getLocalizedMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }


    private NewCookie createCookie(String login) {
        return new NewCookie(new Cookie("X-JWT-AUTH",
                authService.createToken(login, COOKIE_ISSUER, null), "/", null),
                "JWT token", 6000, new Date((new Date()).getTime() + 60000), false, false);

    }
}
