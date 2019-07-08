package org.abondar.experimental.wsboard.ws.impl;

import io.swagger.annotations.Api;
import org.abondar.experimental.wsboard.dao.SecurityCodeDao;
import org.abondar.experimental.wsboard.dao.UserDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.CannotPerformOperationException;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.dao.exception.InvalidHashException;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.ws.service.AuthService;
import org.abondar.experimental.wsboard.ws.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.Path;
import javax.ws.rs.core.Cookie;
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

    @Autowired
    @Qualifier("userDao")
    private UserDao dao;

    @Autowired
    @Qualifier("codeDao")
    private SecurityCodeDao codeDao;

    private AuthService authService;

    public UserServiceImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Response createUser(String login, String email, String firstName, String lastName, String password, String roles) {

        try {
            User user = dao.createUser(login, password, email, firstName, lastName, roles);

            return Response.ok(user).build();
        } catch (CannotPerformOperationException ex) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(LogMessageUtil.HASH_NOT_CREATED).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ex) {
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
        }

    }


    @Override
    public Response updateUser(long id, String firstName, String lastName, String email, String roles) {
        try {
            User user = dao.updateUser(id, firstName, lastName, email, roles, null);

            return Response.ok(user).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ex) {
            return Response.status(Response.Status.NO_CONTENT).entity(ex.getLocalizedMessage()).build();
        }
    }


    @Override
    public Response updateAvatar(long id, byte[] avatar) {
        try {
            User user = dao.updateUser(id, null, null, null, null, avatar);

            return Response.ok(user).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ignored) {
            return Response.ok().build();
        }
    }


    @Override
    public Response updateLogin(String login, long id) {
        try {
            User user = dao.updateLogin(login, id);

            return Response.ok(user).build();
        } catch (DataExistenceException ex) {
            if (ex.getMessage().equals(LogMessageUtil.USER_EXISTS)) {
                return Response.status(Response.Status.CREATED).entity(ex.getLocalizedMessage()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
            }

        } catch (DataCreationException ex) {
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(ex.getLocalizedMessage()).build();
        }
    }


    @Override
    public Response updatePassword(String oldPassword, String newPassword, long id) {
        try {
            User user = dao.updatePassword(oldPassword, newPassword, id);

            return Response.ok(user).build();
        } catch (InvalidHashException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getLocalizedMessage()).build();
        } catch (CannotPerformOperationException ex) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(LogMessageUtil.HASH_NOT_CREATED).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }


    @Override
    public Response deleteUser(long id) {
        try {
            User user = dao.deleteUser(id);

            return Response.ok(user).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ex) {
            return Response.status(Response.Status.NOT_IMPLEMENTED).entity(ex.getLocalizedMessage()).build();
        }
    }


    @Override
    public Response loginUser(String login, String password) {
        try {
            dao.loginUser(login, password);

            return Response.ok().cookie(createLoginCookie(login)).build();
        } catch (InvalidHashException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getLocalizedMessage()).build();
        } catch (CannotPerformOperationException ex) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(LogMessageUtil.HASH_NOT_CREATED).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }


    @Override
    public Response logoutUser(long id) {
        try {
            dao.findUserById(id);

            return Response.ok().cookie(new NewCookie(new Cookie("X-JWT-AUTH", "", "/", ""),
                    "JWT token", 24000, false)).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }


    @Override
    public Response findUserByLogin(String login) {
        try {
            var usr = dao.findUserByLogin(login);
            return Response.ok(usr).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }

    }


    @Override
    public Response resetPassword(long id) {
        try {
            dao.resetPassword(id);
            Response.ok().build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }

        return null;
    }


    @Override
    public Response enterCode(long userId) {
        try {
            codeDao.updateCode(userId);
            Response.ok().build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }

        return null;
    }


    private NewCookie createLoginCookie(String login) {
        return new NewCookie(new Cookie("X-JWT-AUTH",
                authService.createToken(login, COOKIE_ISSUER, null), "/", null),
                "JWT token", 6000, new Date((new Date()).getTime() + 60000), false, true);

    }
}
