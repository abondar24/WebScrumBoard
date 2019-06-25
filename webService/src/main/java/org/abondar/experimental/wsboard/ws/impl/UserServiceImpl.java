package org.abondar.experimental.wsboard.ws.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.abondar.experimental.wsboard.dao.UserDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.CannotPerformOperationException;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.dao.exception.InvalidHashException;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.ws.service.AuthService;
import org.abondar.experimental.wsboard.ws.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
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

/**
 * User service implementation
 *
 * @author a.bondar
 */
@Path("/user")
@Api("User api")
public class UserServiceImpl implements UserService {

    private static final String COOKIE_ISSUER = "borscht systems";
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
    @ApiOperation(
            value = "Create user",
            notes = "Creates a new user based on form data",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User created", response = User.class),
            @ApiResponse(code = 206, message = "Form data is not complete"),
            @ApiResponse(code = 302, message = "User with such login already exists"),
            @ApiResponse(code = 503, message = "Password hash not created")
    })
    @Override
    public Response createUser(@FormParam("login") @ApiParam(required = true) String login,
                               @FormParam("email") @ApiParam(required = true) String email,
                               @FormParam("firstName") @ApiParam(required = true) String firstName,
                               @FormParam("lastName") @ApiParam(required = true) String lastName,
                               @FormParam("password") @ApiParam(required = true) String password,
                               @FormParam("roles") @ApiParam(required = true) String roles) {

        try {
            User user = dao.createUser(login, password, email, firstName, lastName, roles);

            return Response.ok(user).cookie(createLoginCookie(user.getLogin())).build();
        } catch (CannotPerformOperationException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(LogMessageUtil.HASH_NOT_CREATED).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
        }

    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    @ApiOperation(
            value = "Update user",
            notes = "Update user first,last name,email and roles",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User updated", response = User.class),
            @ApiResponse(code = 204, message = ""),
            @ApiResponse(code = 404, message = "User with id not exists"),
            @ApiResponse(code = 406, message = "JWT token is wrong")
    })
    @Override
    public Response updateUser(@FormParam("id") @ApiParam(required = true) long id,
                               @FormParam("firstName") @ApiParam(required = true) String firstName,
                               @FormParam("lastName") @ApiParam(required = true) String lastName,
                               @FormParam("email") @ApiParam(required = true) String email,
                               @FormParam("roles") @ApiParam(required = true) String roles) {
        try {
            User user = dao.updateUser(id, firstName, lastName, email, roles, null);

            return Response.ok(user).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NO_CONTENT).entity(ex.getLocalizedMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update_avatar")
    @ApiOperation(
            value = "Update avatar",
            notes = "Update user avatar",
            consumes = "application/octet-stream",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User avatar updated", response = User.class),
            @ApiResponse(code = 404, message = "User with id not exists"),
            @ApiResponse(code = 406, message = "JWT token is wrong"),
            @ApiResponse(code = 500, message = "Avatar is empty")
    })
    @Override
    public Response updateAvatar(@QueryParam("id") @ApiParam(required = true) long id, byte[] avatar) {
        try {
            User user = dao.updateUser(id, null, null, null, null, avatar);

            return Response.ok(user).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ignored) {
            return Response.ok().build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update_login")
    @ApiOperation(
            value = "Update login",
            notes = "Update user login",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User with updated login", response = User.class),
            @ApiResponse(code = 302, message = "User with login already exists"),
            @ApiResponse(code = 404, message = "User with id not exists"),
            @ApiResponse(code = 406, message = "JWT token is wrong"),
            @ApiResponse(code = 501, message = "User login is empty")
    })
    @Override
    public Response updateLogin(@FormParam("login") @ApiParam(required = true) String login,
                                @FormParam("id") @ApiParam(required = true) long id) {
        try {
            User user = dao.updateLogin(login, id);

            return Response.ok(user).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            if (ex.getMessage().equals(LogMessageUtil.USER_EXISTS)) {
                return Response.status(Response.Status.CREATED).entity(ex.getLocalizedMessage()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
            }

        } catch (DataCreationException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(ex.getLocalizedMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update_password")
    @ApiOperation(
            value = "Update password",
            notes = "Update user password",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User with updated password", response = User.class),
            @ApiResponse(code = 401, message = "User password is wrong"),
            @ApiResponse(code = 404, message = "User with id not exists"),
            @ApiResponse(code = 406, message = "JWT token is wrong"),
            @ApiResponse(code = 503, message = "Password hash not created")
    })
    @Override
    public Response updatePassword(@FormParam("oldPassword") @ApiParam(required = true) String oldPassword,
                                   @FormParam("newPassword") @ApiParam(required = true) String newPassword,
                                   @FormParam("id") @ApiParam(required = true) long id) {
        try {
            User user = dao.updatePassword(oldPassword, newPassword, id);

            return Response.ok(user).build();
        } catch (InvalidHashException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getLocalizedMessage()).build();
        } catch (CannotPerformOperationException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(LogMessageUtil.HASH_NOT_CREATED).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete")
    @ApiOperation(
            value = "Delete",
            notes = "Delete user",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User marked as deleted", response = User.class),
            @ApiResponse(code = 404, message = "User with id not exists"),
            @ApiResponse(code = 406, message = "JWT token is wrong"),
            @ApiResponse(code = 501, message = "User is project owner")
    })
    @Override
    public Response deleteUser(@QueryParam("id") @ApiParam(required = true) long id) {
        try {
            User user = dao.deleteUser(id);

            return Response.ok(user).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(ex.getLocalizedMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login")
    @PermitAll
    @ApiOperation(
            value = "Login",
            notes = "Log in user",
            consumes = "application/x-www-urlformencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User logged in"),
            @ApiResponse(code = 401, message = "User password is wrong"),
            @ApiResponse(code = 404, message = "User with id not exists"),
            @ApiResponse(code = 503, message = "Password hash not created")
    })
    @Override
    public Response loginUser(@FormParam("login") @ApiParam(required = true) String login,
                              @FormParam("password") @ApiParam(required = true) String password) {
        try {
            dao.loginUser(login, password);

            return Response.ok().cookie(createLoginCookie(login)).build();
        } catch (InvalidHashException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getLocalizedMessage()).build();
        } catch (CannotPerformOperationException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(LogMessageUtil.HASH_NOT_CREATED).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/logout")
    @ApiOperation(
            value = "Logout",
            notes = "Log out user",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User logged out"),
            @ApiResponse(code = 404, message = "User with id not exists"),
            @ApiResponse(code = 406, message = "JWT token is wrong"),
    })
    @Override
    public Response logoutUser(@QueryParam("id") @ApiParam(required = true) long id) {
        try {
            dao.findUserById(id);

            return Response.ok().cookie(new NewCookie(new Cookie("X-JWT-AUTH", "", "/", ""),
                    "JWT token", 24000, false)).build();
        } catch (DataExistenceException ex) {
            logger.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }


    private NewCookie createLoginCookie(String login) {
        return new NewCookie(new Cookie("X-JWT-AUTH",
                authService.createToken(login, COOKIE_ISSUER, null), "/", null),
                "JWT token", 6000, new Date((new Date()).getTime() + 60000), false, true);

    }
}
