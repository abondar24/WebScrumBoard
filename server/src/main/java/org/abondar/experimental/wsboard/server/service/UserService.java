package org.abondar.experimental.wsboard.server.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * User CRUD and login web service
 *
 * @author a.bondar
 */
@Path("/user")
@Api("User api")
public interface UserService {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Create user",
            notes = "Creates a new user based on form data",
            consumes = "application/x-www-urlencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User created", response = User.class),
            @ApiResponse(code = 206, message = "Form data is not complete"),
            @ApiResponse(code = 302, message = "User with such login already exists"),
            @ApiResponse(code = 503, message = "Password hash not created")
    })
    Response createUser(@FormParam("login") @ApiParam(required = true) String login,
                        @FormParam("password") @ApiParam(required = true) String password,
                        @FormParam("email") @ApiParam(required = true) String email,
                        @FormParam("firstName") @ApiParam(required = true) String firstName,
                        @FormParam("lastName") @ApiParam(required = true) String lastName,
                        @FormParam("roles") @ApiParam(required = true) String roles);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Update user",
            notes = "Update user first,last name,email and roles",
            consumes = "application/x-www-urlencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User updated", response = User.class),
            @ApiResponse(code = 204, message = ""),
            @ApiResponse(code = 404, message = "User with id not exists")
    })
    Response updateUser(@PathParam("id") @ApiParam(required = true) long id,
                        @FormParam("firstName") @ApiParam(required = true) String firstName,
                        @FormParam("lastName") @ApiParam(required = true) String lastName,
                        @FormParam("email") @ApiParam(required = true) String email,
                        @FormParam("roles") @ApiParam(required = true) String roles);

    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/avatar")
    @ApiOperation(
            value = "Update avatar",
            notes = "Update user avatar",
            consumes = "multipart/mixed",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User avatar updated", response = User.class),
            @ApiResponse(code = 400, message = "Image corrupted"),
            @ApiResponse(code = 404, message = "User with id not exists"),
            @ApiResponse(code = 500, message = "Avatar is empty")
    })
    Response updateAvatar(@PathParam("id") @ApiParam(required = true) long id, MultipartBody avatar);

    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/login")
    @ApiOperation(
            value = "Update login",
            notes = "Update user login",
            consumes = "application/x-www-urlencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User with updated login", response = User.class),
            @ApiResponse(code = 302, message = "User with login already exists"),
            @ApiResponse(code = 404, message = "User with id not exists"),
            @ApiResponse(code = 501, message = "User login is empty")
    })
    Response updateLogin(@FormParam("login") @ApiParam(required = true) String login,
                         @PathParam("id") @ApiParam(required = true) long id);

    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/password")
    @ApiOperation(
            value = "Update password",
            notes = "Update user password",
            consumes = "application/x-www-urlencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User with updated password", response = User.class),
            @ApiResponse(code = 401, message = "User password is wrong"),
            @ApiResponse(code = 404, message = "User with id not exists")
    })
    Response updatePassword(@FormParam("oldPassword") @ApiParam(required = true) String oldPassword,
                            @FormParam("newPassword") @ApiParam(required = true) String newPassword,
                            @PathParam("id") @ApiParam(required = true) long id);


    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @ApiOperation(
            value = "Delete",
            notes = "Delete user",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User marked as deleted", response = User.class),
            @ApiResponse(code = 404, message = "User with id not exists"),
            @ApiResponse(code = 501, message = "User is project owner")
    })
    Response deleteUser(@PathParam("id") @ApiParam(required = true) long id);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login")
    @ApiOperation(
            value = "Login",
            notes = "Log in user",
            consumes = "application/x-www-urlencoded",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User logged in"),
            @ApiResponse(code = 401, message = "User password is wrong"),
            @ApiResponse(code = 404, message = "User with id not exists")
    })
    Response loginUser(@FormParam("login") @ApiParam(required = true) String login,
                       @FormParam("password") @ApiParam(required = true) String password);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/logout")
    @ApiOperation(
            value = "Logout",
            notes = "Log out user",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User logged out"),
            @ApiResponse(code = 404, message = "User with id not exists")
    })
    Response logoutUser(@PathParam("id") @ApiParam(required = true) long id);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{login}")
    @ApiOperation(
            value = "Find",
            notes = "Find user by login",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User found"),
            @ApiResponse(code = 404, message = "User not found")
    })
    Response findUserByLogin(@PathParam("login") @ApiParam(required = true) String login);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find by IDs",
            notes = "Find users by ids",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User found"),
            @ApiResponse(code = 204, message = "Empty result")
    })
    Response findUsersByIds(@QueryParam("id") @ApiParam(required = true)List<Long>ids);


    @PUT
    @Path("/{id}/reset_pwd")
    @ApiOperation(
            value = "Reset password",
            notes = "Reset user password to value 'reset'. " +
                    "Set Accept-Language header to values de,fr,es,ru to get error message in selected language",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Password reset"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 503, message = "Password hash not created")
    })
    Response resetPassword(@PathParam("id") @ApiParam(required = true) long id);


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{userId}/code")
    @PermitAll
    @ApiOperation(
            value = "Update code",
            notes = "Activates security code sent to user",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Code activated"),
            @ApiResponse(code = 400, message = "Wrong code entered"),
            @ApiResponse(code = 404, message = "User or code not found"),
    })
    Response enterCode(@PathParam("userId") @ApiParam(required = true) long userId,
                       @FormParam("code") @ApiParam(required = true) String code);

}
