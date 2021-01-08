package org.abondar.experimental.wsboard.route;

import org.abondar.experimental.wsboard.dao.SprintDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Sprint;
import org.abondar.experimental.wsboard.ws.util.I18nKeyUtil;
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
import java.util.List;
import java.util.Locale;

import static org.abondar.experimental.wsboard.ws.util.RouteConstantUtil.ACCEPT_LANG_HEADER;
import static org.abondar.experimental.wsboard.ws.util.RouteConstantUtil.LOG_HEADERS;

/**
 * Route for contributor route events
 *
 * @author a.bondar
 */
public class SprintServiceRoute extends RouteBuilder {

    @Autowired
    @Qualifier("sprintDao")
    private SprintDao sprintDao;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void configure() throws Exception {

        from("direct:createSprint").routeId("createSprint")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        var startDt = convertDate((String) formData.get(1));
                        var endDt = convertDate((String) formData.get(2));

                        var sprint = sprintDao.createSprint((String) formData.get(0), startDt, endDt,
                                (long) formData.get(3));
                        return Response.ok(sprint).build();

                    } catch (DataCreationException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.WRONG_END_DATE)) {
                            return getLocalizedResponse(lang, I18nKeyUtil.WRONG_END_DATE,Response.Status.RESET_CONTENT);
                        } else {
                            return getLocalizedResponse(lang, I18nKeyUtil.BLANK_DATA,Response.Status.PARTIAL_CONTENT);
                        }
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.SPRINT_EXISTS,Response.Status.FOUND);
                    }
                });

        from("direct:updateSprint").routeId("updateSprint")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        Date startDt = null;
                        if (formData.get(2) != null) {
                            startDt = convertDate((String) formData.get(2));

                        }

                        Date endDt = null;
                        if (formData.get(3) != null) {
                            endDt = convertDate((String) formData.get(3));
                        }

                        var sprint = sprintDao.updateSprint((long) formData.get(0), (String) formData.get(1),
                                startDt, endDt,(Boolean) formData.get(4));

                        return Response.ok(sprint).build();
                    } catch (DataCreationException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.WRONG_END_DATE)) {
                            return getLocalizedResponse(lang, I18nKeyUtil.WRONG_END_DATE,Response.Status.RESET_CONTENT);
                        } else {
                            return getLocalizedResponse(lang, I18nKeyUtil.BLANK_DATA,Response.Status.PARTIAL_CONTENT);
                        }
                    } catch (DataExistenceException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.SPRINT_NOT_EXISTS)) {
                            return getLocalizedResponse(lang, I18nKeyUtil.SPRINT_NOT_EXISTS,Response.Status.NOT_FOUND);
                        } else if (ex.getMessage().equals(LogMessageUtil.SPRINT_EXISTS)) {
                            return getLocalizedResponse(lang, I18nKeyUtil.SPRINT_EXISTS,Response.Status.FOUND);
                        } else {
                            return getLocalizedResponse(lang, I18nKeyUtil.SPRINT_ACTIVE_EXISTS,Response.Status.CONFLICT);
                        }
                    }
                });

        from("direct:getSprintById").routeId("getSprintById")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        var sprint = sprintDao.getSprintById((long) queryData.get(0));
                        return Response.ok(sprint).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.SPRINT_NOT_EXISTS,Response.Status.NOT_FOUND);
                    }
                });

        from("direct:getSprints").routeId("getSprints")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try{
                        List<Sprint>  sprints;
                        if ( (Integer) queryData.get(2)==0){
                             sprints = sprintDao.getSprints((long)queryData.get(0),(int) queryData.get(1), null);

                        } else {
                            sprints = sprintDao.getSprints((long)queryData.get(0),
                                    (int) queryData.get(1), (Integer) queryData.get(2));

                        }
                        if (sprints.isEmpty()) {
                            return Response.status(Response.Status.NO_CONTENT).build();
                        }

                        return Response.ok(sprints).build();
                    } catch (DataExistenceException ex){
                        return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_NOT_EXISTS,Response.Status.NOT_FOUND);
                    }

                });

        from("direct:getCurrentSprint").routeId("getCurrentSprint")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try{
                        var sprints = sprintDao.getCurrentSprint((long)queryData.get(0));
                        if (sprints==null) {
                            return Response.status(Response.Status.NO_CONTENT).build();
                        }

                        return Response.ok(sprints).build();
                    } catch (DataExistenceException ex){
                        return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_NOT_EXISTS,Response.Status.NOT_FOUND);
                    }

                });

        from("direct:countSprints").routeId("countSprints")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        var tasks = sprintDao.countSprints((long) queryData.get(0));

                        return Response.ok(tasks).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_NOT_EXISTS,Response.Status.NOT_FOUND);
                    }


                });

        from("direct:deleteSprint").routeId("deleteSprint")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        sprintDao.deleteSprint((long) queryData.get(0));
                        return Response.ok().build();

                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.SPRINT_NOT_EXISTS,Response.Status.NOT_FOUND);
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
