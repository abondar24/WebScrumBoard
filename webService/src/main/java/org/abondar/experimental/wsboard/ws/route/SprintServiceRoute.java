package org.abondar.experimental.wsboard.ws.route;

import org.abondar.experimental.wsboard.dao.SprintDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
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
                .log(LoggingLevel.DEBUG,LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList formData = (MessageContentsList) bdy;

                    try {
                        var startDt = convertDate((String) formData.get(1));
                        var endDt = convertDate((String) formData.get(2));

                        var sprint = sprintDao.createSprint((String) formData.get(0), startDt, endDt,
                                (long) formData.get(3));
                        return Response.ok(sprint).build();

                    } catch (DataCreationException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.WRONG_END_DATE)) {
                            return Response.status(Response.Status.RESET_CONTENT).entity(ex.getLocalizedMessage()).build();
                        } else {
                            return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
                        }
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
                    }
                });

        from("direct:updateSprint").routeId("updateSprint")
                .log(LoggingLevel.DEBUG,LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList formData = (MessageContentsList) bdy;

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
                                startDt, endDt);

                        return Response.ok(sprint).build();
                    } catch (DataCreationException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.WRONG_END_DATE)) {
                            return Response.status(Response.Status.RESET_CONTENT).entity(ex.getLocalizedMessage()).build();
                        } else {
                            return Response.status(Response.Status.NO_CONTENT).entity(ex.getLocalizedMessage()).build();
                        }
                    } catch (DataExistenceException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.SPRINT_NOT_EXISTS)) {
                            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                        } else {
                            return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
                        }
                    }
                });

        from("direct:getSprintById").routeId("getSprintById")
                .log(LoggingLevel.DEBUG,LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList queryData = (MessageContentsList) bdy;

                    try {
                        var sprint = sprintDao.getSprintById((long) queryData.get(0));
                        return Response.ok(sprint).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }
                });

        from("direct:getSprints").routeId("getSprints")
                .log(LoggingLevel.DEBUG,LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList queryData = (MessageContentsList) bdy;

                    try{
                        var sprints = sprintDao.getSprints((long)queryData.get(0),(int) queryData.get(1), (int) queryData.get(2));
                        if (sprints.isEmpty()) {
                            return Response.status(Response.Status.NO_CONTENT).build();
                        }

                        return Response.ok(sprints).build();
                    } catch (DataExistenceException ex){
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
                    }

                });

        from("direct:countSprints").routeId("countSprints")
                .log(LoggingLevel.DEBUG,LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList queryData = (MessageContentsList) bdy;
                    try {
                        var tasks = sprintDao.countSprints((long) queryData.get(0));

                        return Response.ok(tasks).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }


                });

        from("direct:deleteSprint").routeId("deleteSprint")
                .log(LoggingLevel.DEBUG,LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    try {
                        sprintDao.deleteSprint((long) queryData.get(0));
                        return Response.ok().build();

                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
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
