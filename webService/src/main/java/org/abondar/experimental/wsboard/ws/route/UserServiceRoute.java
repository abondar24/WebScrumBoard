package org.abondar.experimental.wsboard.ws.route;

import org.abondar.experimental.wsboard.dao.SecurityCodeDao;
import org.abondar.experimental.wsboard.dao.UserDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.CannotPerformOperationException;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.dao.exception.InvalidHashException;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.ws.service.AuthService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * Route for user service events
 *
 * @author a.bondar
 */
public class UserServiceRoute extends RouteBuilder {

    private static final String COOKIE_ISSUER = "borscht systems";
    @Autowired
    @Qualifier("userDao")
    private UserDao dao;
    @Autowired
    @Qualifier("codeDao")
    private SecurityCodeDao codeDao;
    @Autowired
    private AuthService authService;

    @Override
    public void configure() throws Exception {


        from("direct:createUser").routeId("createUser")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;

                    try {
                        User user = dao.createUser((String) formData.get(0), (String) formData.get(1),
                                (String) formData.get(2), (String) formData.get(3),
                                (String) formData.get(4), (String) formData.get(5));

                        hdrs.put("emailType", "createUser");
                        hdrs.put("code", codeDao.insertCode(user.getId()));
                        hdrs.put("To", user.getEmail());
                        return Response.ok(user).build();

                    } catch (CannotPerformOperationException ex) {
                        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(LogMessageUtil.HASH_NOT_CREATED).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
                    } catch (DataCreationException ex) {
                        return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
                    }
                })

                .wireTap("direct:sendEmail")
                .removeHeaders("*");

        from("direct:updateUser").routeId("updateUser")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;

                    try {
                        User user = dao.updateUser((long) formData.get(0), (String) formData.get(1),
                                (String) formData.get(2), (String) formData.get(3), (String) formData.get(4), null);


                        return Response.ok(user).build();

                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    } catch (DataCreationException ex) {
                        return Response.status(Response.Status.NO_CONTENT).entity(ex.getLocalizedMessage()).build();
                    }
                });

        from("direct:updateAvatar").routeId("updateAvatar")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;

                    try {

                        //potentially buggy place
                        User user = dao.updateUser((long) formData.get(0), null, null,
                                null, null, (byte[]) formData.get(1));


                        return Response.ok(user).build();

                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
                    } catch (DataCreationException ex) {
                        return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
                    }
                });


        from("direct:updateLogin").routeId("updateLogin")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    try {
                        User user = dao.updateLogin((String) formData.get(0), (long) formData.get(1));
                        hdrs.put("emailType", "updateLogin");
                        hdrs.put("To", user.getEmail());

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

                })
                .wireTap("direct:sendEmail")
                .removeHeaders("*");


        from("direct:updatePassword").routeId("updatePassword")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    try {
                        User user = dao.updatePassword((String) formData.get(0), (String) formData.get(1),
                                (long) formData.get(2));

                        hdrs.put("emailType", "updatePassword");
                        hdrs.put("To", user.getEmail());


                        return Response.ok(user).build();
                    } catch (InvalidHashException ex) {
                        return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getLocalizedMessage()).build();
                    } catch (CannotPerformOperationException ex) {
                        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(LogMessageUtil.HASH_NOT_CREATED).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }
                })
                .wireTap("direct:sendEmail")
                .removeHeaders("*");

        from("direct:deleteUser").routeId("deleteUser")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;

                    try {
                        User user = dao.deleteUser((long) queryData.get(0));
                        hdrs.put("emailType", "deleteUser");
                        hdrs.put("To", user.getEmail());

                        return Response.ok(user).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    } catch (DataCreationException ex) {
                        return Response.status(Response.Status.NOT_IMPLEMENTED).entity(ex.getLocalizedMessage()).build();
                    }

                })
                .wireTap("direct:sendEmail")
                .removeHeaders("*");

        from("direct:loginUser").routeId("login")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    try {
                        dao.loginUser((String) formData.get(0), (String) formData.get(1));

                        return Response.ok().cookie(createLoginCookie((String) formData.get(0))).build();
                    } catch (InvalidHashException ex) {
                        return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getLocalizedMessage()).build();
                    } catch (CannotPerformOperationException ex) {
                        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(LogMessageUtil.HASH_NOT_CREATED).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }
                });


        from("direct:logoutUser").routeId("logout")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    try {
                        dao.findUserById((long) formData.get(0));

                        return Response.ok().cookie(new NewCookie(new Cookie("X-JWT-AUTH", "", "/", ""),
                                "JWT token", 24000, false)).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }
                });


        from("direct:findUserByLogin").routeId("findUserByLogin")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    try {
                        var usr = dao.findUserByLogin((String) formData.get(0));
                        return Response.ok(usr).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }
                });


        from("direct:resetPassword").routeId("resetPassword")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    try {
                        var user = dao.findUserById((long) formData.get(0));
                        dao.resetPassword(((long) formData.get(0)));

                        hdrs.put("emailType", "resetPassword");
                        hdrs.put("To", user.getEmail());

                        return Response.ok().build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }


                })
                .wireTap("direct:sendEmail")
                .removeHeaders("*");


        from("direct:enterCode").routeId("enterCode")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    try {
                        codeDao.updateCode((long) formData.get(0), (long) formData.get(1));
                        return Response.ok().build();
                    } catch (DataExistenceException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.CODE_NOT_EXISTS)) {
                            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                        } else {
                            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getLocalizedMessage()).build();
                        }

                    }

                });


    }

    private NewCookie createLoginCookie(String login) {
        return new NewCookie(new Cookie("X-JWT-AUTH",
                authService.createToken(login, COOKIE_ISSUER, null), "/", null),
                "JWT token", 6000, new Date((new Date()).getTime() + 60000), false, true);

    }
}