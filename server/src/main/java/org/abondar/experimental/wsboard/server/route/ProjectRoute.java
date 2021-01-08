package org.abondar.experimental.wsboard.server.route;

import org.abondar.experimental.wsboard.server.dao.ProjectDao;

import org.abondar.experimental.wsboard.server.exception.DataCreationException;
import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.abondar.experimental.wsboard.server.util.I18nKeyUtil;
import org.abondar.experimental.wsboard.server.util.LogMessageUtil;
import org.abondar.experimental.wsboard.server.util.RouteConstantUtil;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;

import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Route for project service events
 *
 * @author a.bondar
 */
public class ProjectRoute extends RouteBuilder {

    @Autowired
    @Qualifier("projectDao")
    private ProjectDao projectDao;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void configure() throws Exception {

        from("direct:createProject").routeId("createProject")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        var prj = projectDao.createProject((String) formData.get(0),
                                convertDate((String) formData.get(1)));

                        return Response.ok(prj).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_EXISTS,Response.Status.FOUND);
                    } catch (DataCreationException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.BLANK_DATA,Response.Status.PARTIAL_CONTENT);
                    }
                });


        from("direct:updateProject").routeId("updateProject")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        Date endDt = null;
                        if (formData.get(4) != null) {
                            endDt = convertDate((String) formData.get(4));
                        }

                        var prj = projectDao.updateProject((long) formData.get(0), (String) formData.get(1),
                                (String) formData.get(2), (Boolean) formData.get(3), endDt,(String) formData.get(5));
                        return Response.ok(prj).build();
                    } catch (DataExistenceException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.PROJECT_NOT_EXISTS)) {
                            return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_NOT_EXISTS,Response.Status.NOT_FOUND);
                        } else {
                            return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_EXISTS,Response.Status.FOUND);
                        }

                    } catch (DataCreationException ex) {
                        switch (ex.getMessage()) {
                            case LogMessageUtil.WRONG_END_DATE:
                                return getLocalizedResponse(lang,
                                        I18nKeyUtil.WRONG_END_DATE,Response.Status.RESET_CONTENT);
                            case LogMessageUtil.PARSE_DATE_FAILED:
                                return getLocalizedResponse(lang,
                                        I18nKeyUtil.PARSE_DATE_FAILED,Response.Status.PARTIAL_CONTENT);
                            default:
                                return getLocalizedResponse(lang,
                                        I18nKeyUtil.PROJECT_CANNOT_BE_REACTIVATED,Response.Status.MOVED_PERMANENTLY);
                        }
                    }

                });



        from("direct:deleteProject").routeId("deleteProject")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        projectDao.deleteProject((long) queryData.get(0));
                        return Response.ok().build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_NOT_EXISTS,Response.Status.NOT_FOUND);
                    }
                });


        from("direct:findProjectById").routeId("findProjectById")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        var prj = projectDao.findProjectById((long) queryData.get(0));
                        return Response.ok(prj).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_NOT_EXISTS,Response.Status.NOT_FOUND);
                    }
                });

        from("direct:findUserProjects").routeId("findUserProjects")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        var prj = projectDao.findUserProjects((long) queryData.get(0));

                        if (prj.isEmpty()){
                            return Response.status(Response.Status.NO_CONTENT).build();
                        }

                        return Response.ok(prj).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_NOT_EXISTS,Response.Status.NOT_FOUND);
                    }
                });

    }

    /**
     * Method for converting string to date
     *
     * @param strDate - date as string
     * @return Date
     * @throws DataCreationException - string parsing failed
     */
    private Date convertDate(String strDate) throws DataCreationException {
        var format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            return format.parse(strDate);
        } catch (ParseException ex) {
            throw new DataCreationException(LogMessageUtil.PARSE_DATE_FAILED);
        }

    }

    /**
     * Returns localized response or default if language not found
     * @param lang - language code
     * @param key - message key
     * @param status - HTTP status
     * @return - Response status with localized message
     */
    private Response getLocalizedResponse(String lang,String key,Response.Status status){
        Locale locale = new Locale.Builder().setLanguage(lang).build();
        return Response.status(status)
                .entity(messageSource.getMessage(key, null, locale)).build();

    }

}



