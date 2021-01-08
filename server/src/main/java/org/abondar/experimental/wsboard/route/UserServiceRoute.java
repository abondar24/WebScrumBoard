package org.abondar.experimental.wsboard.route;

import org.abondar.experimental.wsboard.dao.SecurityCodeDao;
import org.abondar.experimental.wsboard.dao.UserDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.CannotPerformOperationException;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.dao.exception.InvalidHashException;
import org.abondar.experimental.wsboard.datamodel.user.user.User;
import org.abondar.experimental.wsboard.ws.service.AuthService;
import org.abondar.experimental.wsboard.ws.util.I18nKeyUtil;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;

import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.abondar.experimental.wsboard.ws.util.RouteConstantUtil.ACCEPT_LANG_HEADER;
import static org.abondar.experimental.wsboard.ws.util.RouteConstantUtil.EMAIL_TYPE_HEADER;
import static org.abondar.experimental.wsboard.ws.util.RouteConstantUtil.LOG_HEADERS;
import static org.abondar.experimental.wsboard.ws.util.RouteConstantUtil.SEND_EMAIL_ENDPOINT;

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

    @Autowired
    private MessageSource messageSource;

    @Override
    public void configure() throws Exception {


        from("direct:createUser").routeId("createUser")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        User user = dao.createUser((String) formData.get(0), (String) formData.get(1),
                                (String) formData.get(2), (String) formData.get(3),
                                (String) formData.get(4), (String) formData.get(5));

                        hdrs.put(RouteConstantUtil.EMAIL_TYPE_HEADER, "createUser");
                        hdrs.put("code", codeDao.insertCode(user.getId()));
                        hdrs.put("To", user.getEmail());
                        return Response.ok(user).build();

                    } catch (CannotPerformOperationException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.HASH_NOT_CREATED,
                                Response.Status.SERVICE_UNAVAILABLE);
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_EXISTS, Response.Status.FOUND);
                    } catch (DataCreationException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.BLANK_DATA, Response.Status.PARTIAL_CONTENT);
                    }
                })

                .wireTap(RouteConstantUtil.SEND_EMAIL_ENDPOINT)
                .removeHeaders("*");

        from("direct:updateUser").routeId("updateUser")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        User user = dao.updateUser((long) formData.get(0), (String) formData.get(1),
                                (String) formData.get(2), (String) formData.get(3), (String) formData.get(4), null);


                        return Response.ok(user).build();

                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_NOT_EXISTS, Response.Status.NOT_FOUND);
                    } catch (DataCreationException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_NO_ROLES, Response.Status.NO_CONTENT);
                    }
                });

        from("direct:updateAvatar").routeId("updateAvatar")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MultipartBody mBody = (MultipartBody) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    var id = (long) hdrs.get("id");

                    try {

                        User user = dao.updateUser(id, null, null,
                                null, null, readImage(mBody.getAllAttachments().get(0)));


                        return Response.ok(user).build();

                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_NOT_EXISTS, Response.Status.NOT_FOUND);
                    } catch (DataCreationException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_NO_ROLES, Response.Status.NO_CONTENT);
                    } catch (IOException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_AVATAR_BAD, Response.Status.BAD_REQUEST);
                    }
                });


        from("direct:updateLogin").routeId("updateLogin")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        User user = dao.updateLogin((String) formData.get(0), (long) formData.get(1));
                        hdrs.put(RouteConstantUtil.EMAIL_TYPE_HEADER, "updateLogin");
                        hdrs.put("To", user.getEmail());
                        hdrs.put("firstName", user.getFirstName());
                        hdrs.put("login", user.getLogin());

                        return Response.ok(user).build();

                    } catch (DataExistenceException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.USER_EXISTS)) {
                            return getLocalizedResponse(lang, I18nKeyUtil.USER_EXISTS, Response.Status.FOUND);
                        } else {
                            return getLocalizedResponse(lang, I18nKeyUtil.USER_NOT_EXISTS, Response.Status.NOT_FOUND);
                        }

                    } catch (DataCreationException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_EMTPY_LOGIN, Response.Status.NOT_IMPLEMENTED);
                    }

                })
                .wireTap(RouteConstantUtil.SEND_EMAIL_ENDPOINT)
                .removeHeaders("*");


        from("direct:updatePassword").routeId("updatePassword")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        User user = dao.updatePassword((String) formData.get(0), (String) formData.get(1),
                                (long) formData.get(2));

                        hdrs.put(RouteConstantUtil.EMAIL_TYPE_HEADER, "updatePassword");
                        hdrs.put("To", user.getEmail());
                        hdrs.put("firstName", user.getFirstName());
                        hdrs.put("login", user.getLogin());


                        return Response.ok(user).build();
                    } catch (InvalidHashException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_UNAUTHORIZED, Response.Status.UNAUTHORIZED);

                    } catch (CannotPerformOperationException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.HASH_NOT_CREATED,
                                Response.Status.SERVICE_UNAVAILABLE);
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_NOT_EXISTS, Response.Status.NOT_FOUND);
                    }
                })
                .wireTap(RouteConstantUtil.SEND_EMAIL_ENDPOINT)
                .removeHeaders("*");

        from("direct:deleteUser").routeId("deleteUser")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        User user = dao.deleteUser((long) queryData.get(0));
                        hdrs.put(RouteConstantUtil.EMAIL_TYPE_HEADER, "deleteUser");
                        hdrs.put("To", user.getEmail());

                        return Response.ok(user).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_NOT_EXISTS, Response.Status.NOT_FOUND);
                    } catch (DataCreationException ex) {
                        return getLocalizedResponse(lang,
                                I18nKeyUtil.USER_IS_PROJECT_OWNER, Response.Status.NOT_IMPLEMENTED);
                    }

                })
                .wireTap(RouteConstantUtil.SEND_EMAIL_ENDPOINT)
                .removeHeaders("*");

        from("direct:loginUser").routeId("login")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        return Response.ok().header("Authorization",
                                "JWT " + authService.authorizeUser((String) formData.get(0), (String) formData.get(1)))
                                .build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_NOT_EXISTS, Response.Status.NOT_FOUND);
                    }
                })
                .removeHeader("password")
                .removeHeader("login");


        from("direct:logoutUser").routeId("logout")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        dao.findUserById((long) formData.get(0));

                        return Response.ok().cookie().build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_NOT_EXISTS, Response.Status.NOT_FOUND);
                    }
                });


        from("direct:findUserByLogin").routeId("findUserByLogin")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        var usr = dao.findUserByLogin((String) formData.get(0));

                        return Response.ok(usr).build();

                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_NOT_EXISTS, Response.Status.NOT_FOUND);
                    }
                });

        from("direct:findUsersByIds").routeId("findUsersByIds")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    var usrs = dao.findUsersByIds((List<Long>) formData.get(0));
                    if (usrs.isEmpty()) {
                        return Response.status(Response.Status.NO_CONTENT).build();
                    }

                    return Response.ok(usrs).build();
                });


        from("direct:resetPassword").routeId("resetPassword")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        var user = dao.findUserById((long) formData.get(0));
                        dao.resetPassword(((long) formData.get(0)));

                        hdrs.put("emailType", "resetPassword");
                        hdrs.put("code", codeDao.insertCode(user.getId()));
                        hdrs.put("To", user.getEmail());
                        hdrs.put("login", user.getLogin());

                        return Response.ok().build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_NOT_EXISTS, Response.Status.NOT_FOUND);
                    } catch (CannotPerformOperationException ex) {
                        return getLocalizedResponse(lang,
                                I18nKeyUtil.HASH_NOT_CREATED, Response.Status.SERVICE_UNAVAILABLE);
                    }


                })
                .wireTap(RouteConstantUtil.SEND_EMAIL_ENDPOINT)
                .removeHeaders("*");


        from("direct:enterCode").routeId("enterCode")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        codeDao.enterCode((long) formData.get(0), (long) formData.get(1));
                        return Response.ok().build();
                    } catch (DataExistenceException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.CODE_NOT_EXISTS)) {
                            return getLocalizedResponse(lang, I18nKeyUtil.CODE_NOT_EXISTS, Response.Status.NOT_FOUND);
                        } else {
                            return getLocalizedResponse(lang, I18nKeyUtil.CODE_NOT_MATCHES, Response.Status.BAD_REQUEST);
                        }

                    }

                });


    }

    private String readImage(Attachment attachment) throws IOException {
        var dataHandler = attachment.getDataHandler();
        var is = dataHandler.getInputStream();
        var bos = new ByteArrayOutputStream();
        final byte[] buffer = new byte[4096];

        for (int read = is.read(buffer); read > 0; read = is.read(buffer)) {
            bos.write(buffer, 0, read);
        }

        return new String(bos.toByteArray());
    }

    /**
     * Returns localized response or default if language not found
     *
     * @param lang   - language code
     * @param key    - message key
     * @param status - HTTP status
     * @return - Response status with localized message
     */
    private Response getLocalizedResponse(String lang, String key, Response.Status status) {
        Locale locale = new Locale.Builder().setLanguage(lang).build();
        return Response.status(status)
                .entity(messageSource.getMessage(key, null, locale)).build();

    }
}
