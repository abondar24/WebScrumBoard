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
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.abondar.experimental.wsboard.ws.route.RouteConstantUtil.EMAIL_TYPE_HEADER;
import static org.abondar.experimental.wsboard.ws.route.RouteConstantUtil.LOG_HEADERS;
import static org.abondar.experimental.wsboard.ws.route.RouteConstantUtil.SEND_EMAIL_ENDPOINT;

/**
 * Route for user service events
 *
 * @author a.bondar
 */
public class UserServiceRoute extends RouteBuilder {

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
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;

                    try {
                        User user = dao.createUser((String) formData.get(0), (String) formData.get(1),
                                (String) formData.get(2), (String) formData.get(3),
                                (String) formData.get(4), (String) formData.get(5));

                        hdrs.put(EMAIL_TYPE_HEADER, "createUser");
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

                .wireTap(SEND_EMAIL_ENDPOINT)
                .removeHeaders("*");

        from("direct:updateUser").routeId("updateUser")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
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
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;

                    try {

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
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    try {
                        User user = dao.updateLogin((String) formData.get(0), (long) formData.get(1));
                        hdrs.put(EMAIL_TYPE_HEADER, "updateLogin");
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
                .wireTap(SEND_EMAIL_ENDPOINT)
                .removeHeaders("*");


        from("direct:updatePassword").routeId("updatePassword")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    try {
                        User user = dao.updatePassword((String) formData.get(0), (String) formData.get(1),
                                (long) formData.get(2));

                        hdrs.put(EMAIL_TYPE_HEADER, "updatePassword");
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
                .wireTap(SEND_EMAIL_ENDPOINT)
                .removeHeaders("*");

        from("direct:deleteUser").routeId("deleteUser")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;

                    try {
                        User user = dao.deleteUser((long) queryData.get(0));
                        hdrs.put(EMAIL_TYPE_HEADER, "deleteUser");
                        hdrs.put("To", user.getEmail());

                        return Response.ok(user).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    } catch (DataCreationException ex) {
                        return Response.status(Response.Status.NOT_IMPLEMENTED).entity(ex.getLocalizedMessage()).build();
                    }

                })
                .wireTap(SEND_EMAIL_ENDPOINT)
                .removeHeaders("*");

        from("direct:loginUser").routeId("login")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    try {
                        return Response.ok().header("Authorization",
                               "JWT "+ authService.authorizeUser((String) formData.get(0), (String) formData.get(1)))
                                .build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }
                })
                .removeHeader("password")
                .removeHeader("login");


        from("direct:logoutUser").routeId("logout")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    try {
                        dao.findUserById((long) formData.get(0));

                        return Response.ok().cookie().build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }
                });


        from("direct:findUserByLogin").routeId("findUserByLogin")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
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

        from("direct:findUsersByIds").routeId("findUsersByIds")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    var usrs = dao.findUsersByIds((List<Long>) formData.get(0));
                    if (usrs.isEmpty()){
                        return Response.status(Response.Status.NO_CONTENT);
                    }

                    return Response.ok(usrs).build();
                });


        from("direct:resetPassword").routeId("resetPassword")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    try {
                        var user = dao.findUserById((long) formData.get(0));
                        dao.resetPassword(((long) formData.get(0)));

                        hdrs.put("emailType", "resetPassword");
                        hdrs.put("code", codeDao.insertCode(user.getId()));
                        hdrs.put("To", user.getEmail());

                        return Response.ok().build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }


                })
                .wireTap(SEND_EMAIL_ENDPOINT)
                .removeHeaders("*");


        from("direct:enterCode").routeId("enterCode")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    try {
                        codeDao.enterCode((long) formData.get(0), (long) formData.get(1));
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

}
