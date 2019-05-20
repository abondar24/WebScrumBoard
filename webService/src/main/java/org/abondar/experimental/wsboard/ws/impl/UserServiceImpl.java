package org.abondar.experimental.wsboard.ws.impl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(
            summary = "Create user",
            description = "Creates a new user based on form data",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User created",
                            content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "204", description = "Form data is not complete"),
                    @ApiResponse(responseCode = "302", description = "User with such login already exists"),
                    @ApiResponse(responseCode = "503", description = "Password hash not created")
            }
    )
    @Override
    public Response createUser(@FormParam("login") @Parameter(description = "User login", required = true) String login,
                               @FormParam("email") @Parameter(description = "User email", required = true) String email,
                               @FormParam("firstName") @Parameter(description = "First name of user", required = true) String firstName,
                               @FormParam("lastName") @Parameter(description = "Last name of user", required = true) String lastName,
                               @FormParam("password") @Parameter(description = "User password", required = true) String password,
                               @FormParam("roles") @Parameter(description = "Comma separated list of user roles", required = true) String roles) {

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
            return Response.status(Response.Status.NO_CONTENT).entity(ex.getLocalizedMessage()).build();
        }

    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    @Operation(
            summary = "Update user",
            description = "Update user first,last name,email and roles",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User updated",
                            content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "204"),
                    @ApiResponse(responseCode = "404", description = "User with id not exists")
            }
    )
    @Override
    public Response updateUser(@FormParam("id") @Parameter(description = "User ID", required = true) long id,
                               @FormParam("firstName") @Parameter(description = "First name of user") String firstName,
                               @FormParam("lastName") @Parameter(description = "Last name of user") String lastName,
                               @FormParam("email") @Parameter(description = "User email") String email,
                               @FormParam("roles") @Parameter(description = "List of user roles") String roles) {
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
    @Operation(
            summary = "Update avatar",
            description = "Update user avatar",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User avatar updated",
                            content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "204", description = "Avatar is empty"),
                    @ApiResponse(responseCode = "404", description = "User with id not exists")
            }
    )
    @Override
    public Response updateAvatar(@QueryParam("id") @Parameter(description = "User ID", required = true) long id,
                                 @RequestBody(description = "User avatar as byte array") byte[] avatar) {
        try {
            User user = dao.updateUser(id, null, null, null, null, avatar);

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
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update_login")
    @Operation(
            summary = "Update login",
            description = "Update user login",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User with updated login",
                            content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "302", description = "User with login already exists"),
                    @ApiResponse(responseCode = "404", description = "User with id not exists"),
                    @ApiResponse(responseCode = "501", description = "User login is empty")
            }
    )
    @Override
    public Response updateLogin(@FormParam("login") @Parameter(description = "User login", required = true) String login,
                                @FormParam("id") @Parameter(description = "User ID", required = true) long id) {
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
    @Operation(
            summary = "Update password",
            description = "Update user password",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User with updated password",
                            content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "401", description = "User password is wrong"),
                    @ApiResponse(responseCode = "404", description = "User with id not exists"),
                    @ApiResponse(responseCode = "503", description = "Password hash not created")
            }
    )
    @Override
    public Response updatePassword(@FormParam("oldPassword") @Parameter(description = "Current user password", required = true) String oldPassword,
                                   @FormParam("newPassword") @Parameter(description = "New user password", required = true) String newPassword,
                                   @FormParam("id") @Parameter(description = "User ID", required = true) long id) {
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
    @Operation(
            summary = "Delete",
            description = "Delete user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User marked as deleted",
                            content = @Content(schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "404", description = "User with id not exists"),
                    @ApiResponse(responseCode = "501", description = "User is project owner")
            }
    )
    @Override
    public Response deleteUser(@QueryParam("id") @Parameter(description = "User ID", required = true) long id) {
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
    @Operation(
            summary = "Login",
            description = "Log in user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User logged in"),
                    @ApiResponse(responseCode = "401", description = "User password is wrong"),
                    @ApiResponse(responseCode = "404", description = "User with id not exists"),
                    @ApiResponse(responseCode = "503", description = "Password hash not created")
            }
    )
    @Override
    public Response loginUser(@FormParam("login") @Parameter(description = "User login", required = true) String login,
                              @FormParam("password") @Parameter(description = "User password", required = true) String password) {
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
    @Operation(
            summary = "Logout",
            description = "Log out user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User logged out"),
                    @ApiResponse(responseCode = "404", description = "User with id not exists")
            }
    )
    @Override
    public Response logoutUser(@QueryParam("id") @Parameter(description = "User ID", required = true) long id) {
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
