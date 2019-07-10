package org.abondar.experimental.wsboard.ws.route;

import org.abondar.experimental.wsboard.dao.TaskDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Route for task service events
 *
 * @author a.bondar
 */
public class TaskServiceRoute extends RouteBuilder {

    @Autowired
    @Qualifier("taskDao")
    private TaskDao taskDao;

    @Override
    public void configure() throws Exception {
        from("direct:createTask").routeId("createTask")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;

                    try {
                        Date stDate = convertDate((String) formData.get(1));
                        var task = taskDao.createTask((long) formData.get(0), stDate, (boolean) formData.get(2));
                        return Response.ok(task).build();

                    } catch (DataCreationException ex) {
                        return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }

                });

        from("direct:updateTask").routeId("updateTask")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;

                    try {
                        var task = taskDao.updateTask((long) formData.get(0), (Long) formData.get(1),
                                (boolean) formData.get(2), (Integer) formData.get(3));
                        return Response.ok(task).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();

                    }

                });


        from("direct:updateTaskSprint").routeId("updateTaskSprint")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;

                    try {
                        var task = taskDao.updateTaskSprint((long) formData.get(0), (long) formData.get(1));
                        return Response.ok(task).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }
                });


        from("direct:updateTaskState").routeId("updateTaskState")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;

                    try {
                        var task = taskDao.updateTaskState((long) formData.get(0), (String) formData.get(0));
                        return Response.ok(task).build();
                    } catch (DataExistenceException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.TASK_NOT_EXISTS)) {
                            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                        } else {
                            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getLocalizedMessage()).build();
                        }
                    } catch (DataCreationException ex) {
                        switch (ex.getMessage()) {
                            case LogMessageUtil.TASK_ALREADY_COMPLETED:
                                return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
                            case LogMessageUtil.TASK_ALREADY_CREATED:
                                return Response.status(Response.Status.CREATED).entity(ex.getLocalizedMessage()).build();
                            case LogMessageUtil.TASK_WRONG_STATE_AFTER_PAUSE:
                                return Response.status(Response.Status.CONFLICT).entity(ex.getLocalizedMessage()).build();
                            case LogMessageUtil.TASK_DEV_OPS_NOT_ENABLED:
                                return Response.status(Response.Status.NO_CONTENT).entity(ex.getLocalizedMessage()).build();
                            case LogMessageUtil.TASK_MOVE_NOT_AVAILABLE:
                                return Response.status(Response.Status.NOT_IMPLEMENTED).entity(ex.getLocalizedMessage()).build();
                            case LogMessageUtil.TASK_CONTRIBUTOR_UPDATE:
                                return Response.status(Response.Status.ACCEPTED).entity(ex.getLocalizedMessage()).build();

                        }
                    }

                    return null;
                });

        from("direct:deleteTask").routeId("deleteTask")
                .log("${headers}")
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
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;

                    try {
                        var task = taskDao.getTaskById((long) queryData.get(0));
                        return Response.ok(task).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }

                });

        from("direct:getTasksForProject").routeId("getTasksForProject")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;

                    try {
                        var tasks = taskDao.getTasksForProject((long) queryData.get(0),
                                (int) queryData.get(1), (int) queryData.get(2));
                        if (tasks.isEmpty()) {
                            return Response.status(Response.Status.NO_CONTENT).build();
                        }

                        return Response.ok(tasks).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }

                });

        from("direct:getTasksForContributor").routeId("getTasksForContributor")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;

                    try {
                        var tasks = taskDao.getTasksForContributor((long) queryData.get(0),
                                (int) queryData.get(1), (int) queryData.get(2));
                        if (tasks.isEmpty()) {
                            return Response.status(Response.Status.NO_CONTENT).build();
                        }

                        return Response.ok(tasks).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }

                });

        from("direct:getTasksForUser").routeId("getTasksForUser")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;

                    try {
                        var tasks = taskDao.getTasksForUser((long) queryData.get(0),
                                (int) queryData.get(1), (int) queryData.get(2));
                        if (tasks.isEmpty()) {
                            return Response.status(Response.Status.NO_CONTENT).build();
                        }

                        return Response.ok(tasks).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }

                });

        from("direct:getTasksForSprint").routeId("getTasksForSprint")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;

                    try {
                        var tasks = taskDao.getTasksForSprint((long) queryData.get(0),
                                (int) queryData.get(1), (int) queryData.get(2));
                        if (tasks.isEmpty()) {
                            return Response.status(Response.Status.NO_CONTENT).build();
                        }

                        return Response.ok(tasks).build();
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
}
