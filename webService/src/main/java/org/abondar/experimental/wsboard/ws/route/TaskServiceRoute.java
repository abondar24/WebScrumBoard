package org.abondar.experimental.wsboard.ws.route;

import org.abondar.experimental.wsboard.dao.TaskDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
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
import java.util.Locale;

import static org.abondar.experimental.wsboard.ws.util.RouteConstantUtil.LOG_HEADERS;

/**
 * Route for task service events
 *
 * @author a.bondar
 */
public class TaskServiceRoute extends RouteBuilder {

    @Autowired
    @Qualifier("taskDao")
    private TaskDao taskDao;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void configure() throws Exception {
        from("direct:createTask").routeId("createTask")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get("Accepted-language");

                    try {
                        Date stDate = convertDate((String) formData.get(1));
                        var task = taskDao.createTask((long) formData.get(0), stDate, (boolean) formData.get(2),
                                (String) formData.get(3), (String) formData.get(4));
                        return Response.ok(task).build();

                    } catch (DataCreationException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.PARSE_DATE_FAILED, Response.Status.PARTIAL_CONTENT);
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.CONTRIBUTOR_NOT_EXISTS, Response.Status.NOT_FOUND);
                    }

                });

        from("direct:updateTask").routeId("updateTask")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get("Accepted-language");

                    try {
                        var task = taskDao.updateTask((long) formData.get(0), (Long) formData.get(1),
                                (boolean) formData.get(2), (Integer) formData.get(3),
                                (String) formData.get(4), (String) formData.get(5));
                        return Response.ok(task).build();
                    } catch (DataExistenceException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.TASK_NOT_EXISTS)) {
                            return getLocalizedResponse(lang, I18nKeyUtil.TASK_NOT_EXISTS, Response.Status.NOT_FOUND);
                        } else {
                            return getLocalizedResponse(lang,
                                    I18nKeyUtil.CONTRIBUTOR_NOT_EXISTS, Response.Status.NOT_FOUND);
                        }

                    }

                });


        from("direct:updateTaskSprint").routeId("updateTaskSprint")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get("Accepted-language");

                    try {
                        var task = taskDao.updateTaskSprint((long) formData.get(0), (long) formData.get(1));
                        return Response.ok(task).build();
                    } catch (DataExistenceException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.TASK_NOT_EXISTS)) {
                            return getLocalizedResponse(lang, I18nKeyUtil.TASK_NOT_EXISTS, Response.Status.NOT_FOUND);
                        } else {
                            return getLocalizedResponse(lang,
                                    I18nKeyUtil.SPRINT_NOT_EXISTS, Response.Status.NOT_FOUND);
                        }
                    }
                });


        from("direct:updateTaskState").routeId("updateTaskState")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get("Accepted-language");

                    try {
                        var task = taskDao.updateTaskState((long) formData.get(0), (String) formData.get(1));
                        return Response.ok(task).build();
                    } catch (DataExistenceException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.TASK_NOT_EXISTS)) {
                            return getLocalizedResponse(lang, I18nKeyUtil.TASK_NOT_EXISTS, Response.Status.NOT_FOUND);
                        } else {
                            return getLocalizedResponse(lang,
                                    I18nKeyUtil.TASK_STATE_UNKNOWN, Response.Status.BAD_REQUEST);

                        }
                    } catch (DataCreationException ex) {
                        switch (ex.getMessage()) {
                            case LogMessageUtil.TASK_ALREADY_COMPLETED:
                                return getLocalizedResponse(lang,
                                        I18nKeyUtil.TASK_ALREADY_COMPLETED, Response.Status.FOUND);
                            case LogMessageUtil.TASK_ALREADY_CREATED:
                                return getLocalizedResponse(lang,
                                        I18nKeyUtil.TASK_ALREADY_CREATED, Response.Status.CREATED);
                            case LogMessageUtil.TASK_WRONG_STATE_AFTER_PAUSE:
                                return getLocalizedResponse(lang,
                                        I18nKeyUtil.TASK_WRONG_STATE_AFTER_PAUSE, Response.Status.CONFLICT);
                            case LogMessageUtil.TASK_DEV_OPS_NOT_ENABLED:
                                return getLocalizedResponse(lang,
                                        I18nKeyUtil.TASK_DEV_OPS_NOT_ENABLED, Response.Status.NO_CONTENT);
                            case LogMessageUtil.TASK_MOVE_NOT_AVAILABLE:
                                return getLocalizedResponse(lang,
                                        I18nKeyUtil.TASK_MOVE_NOT_AVAILABLE, Response.Status.NOT_IMPLEMENTED);
                            case LogMessageUtil.TASK_CONTRIBUTOR_UPDATE:
                                return getLocalizedResponse(lang,
                                        I18nKeyUtil.TASK_CONTRIBUTOR_UPDATE, Response.Status.ACCEPTED);
                            default:
                                return Response.status(Response.Status.FORBIDDEN).build();
                        }
                    }
                });

        from("direct:deleteTask").routeId("deleteTask")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;

                    if (taskDao.deleteTask((long) queryData.get(0))) {
                        return Response.ok().build();
                    } else {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                });


        from("direct:getTaskById").routeId("getTaskById")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get("Accepted-language");

                    try {
                        var task = taskDao.getTaskById((long) queryData.get(0));
                        return Response.ok(task).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.TASK_NOT_EXISTS, Response.Status.NOT_FOUND);
                    }

                });

        from("direct:getTasksForProject").routeId("getTasksForProject")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get("Accepted-language");

                    try {
                        var tasks = taskDao.getTasksForProject((long) queryData.get(0),
                                (int) queryData.get(1), (int) queryData.get(2));
                        if (tasks.isEmpty()) {
                            return Response.status(Response.Status.NO_CONTENT).build();
                        }

                        return Response.ok(tasks).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_NOT_EXISTS, Response.Status.NOT_FOUND);
                    }

                });

        from("direct:getTasksForContributor").routeId("getTasksForContributor")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get("Accepted-language");

                    try {
                        var tasks = taskDao.getTasksForContributor((long) queryData.get(0),
                                (int) queryData.get(1), (int) queryData.get(2));
                        if (tasks.isEmpty()) {
                            return Response.status(Response.Status.NO_CONTENT).build();
                        }

                        return Response.ok(tasks).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.CONTRIBUTOR_NOT_EXISTS, Response.Status.NOT_FOUND);
                    }

                });

        from("direct:countContributorTasks").routeId("countContributorTasks")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get("Accepted-language");

                    try {
                        var tasks = taskDao.countContributorTasks((long) queryData.get(0));

                        return Response.ok(tasks).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.CONTRIBUTOR_NOT_EXISTS, Response.Status.NOT_FOUND);
                    }


                });

        from("direct:getTasksForUser").routeId("getTasksForUser")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get("Accepted-language");

                    try {
                        var tasks = taskDao.getTasksForUser((long) queryData.get(0),
                                (int) queryData.get(1), (int) queryData.get(2));
                        if (tasks.isEmpty()) {
                            return Response.status(Response.Status.NO_CONTENT).build();
                        }

                        return Response.ok(tasks).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_NOT_EXISTS, Response.Status.NOT_FOUND);
                    }

                });

        from("direct:countUserTasks").routeId("countUserTasks")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get("Accepted-language");

                    try {
                        var tasks = taskDao.countUserTasks((long) queryData.get(0));

                        return Response.ok(tasks).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_NOT_EXISTS, Response.Status.NOT_FOUND);
                    }


                });

        from("direct:getTasksForSprint").routeId("getTasksForSprint")
                .log(LoggingLevel.DEBUG, LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get("Accepted-language");

                    try {
                        var tasks = taskDao.getTasksForSprint((long) queryData.get(0),
                                (int) queryData.get(1), (int) queryData.get(2));
                        if (tasks.isEmpty()) {
                            return Response.status(Response.Status.NO_CONTENT).build();
                        }

                        return Response.ok(tasks).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.SPRINT_NOT_EXISTS, Response.Status.NOT_FOUND);
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
