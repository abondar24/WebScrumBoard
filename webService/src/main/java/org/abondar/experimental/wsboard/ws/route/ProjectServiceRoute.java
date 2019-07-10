package org.abondar.experimental.wsboard.ws.route;

import org.abondar.experimental.wsboard.dao.ProjectDao;
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
 * Route for project service events
 *
 * @author a.bondar
 */
public class ProjectServiceRoute extends RouteBuilder {

    @Autowired
    @Qualifier("projectDao")
    private ProjectDao projectDao;

    @Override
    public void configure() throws Exception {

        from("direct:createProject").routeId("createProject")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;

                    try {
                        var prj = projectDao.createProject((String) formData.get(0),
                                convertDate((String) formData.get(1)));

                        return Response.ok(prj).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
                    } catch (DataCreationException ex) {
                        return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
                    }
                });


        from("direct:updateProject").routeId("updateProject")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList formData = (MessageContentsList) bdy;

                    try {
                        Date endDt = null;
                        if (formData.get(4) != null) {
                            endDt = convertDate((String) formData.get(4));
                        }

                        var prj = projectDao.updateProject((long) formData.get(0), (String) formData.get(1),
                                (String) formData.get(2), (Boolean) formData.get(3), endDt);
                        return Response.ok(prj).build();
                    } catch (DataExistenceException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.PROJECT_NOT_EXISTS)) {
                            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                        } else {
                            return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
                        }

                    } catch (DataCreationException ex) {
                        switch (ex.getMessage()) {
                            case LogMessageUtil.WRONG_END_DATE:
                                return Response.status(Response.Status.RESET_CONTENT).entity(ex.getLocalizedMessage()).build();
                            case LogMessageUtil.PARSE_DATE_FAILED:
                                return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
                            default:
                                return Response.status(Response.Status.MOVED_PERMANENTLY).entity(ex.getLocalizedMessage()).build();

                        }
                    }

                });



        from("direct:deleteProject").routeId("deleteProject")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList queryData = (MessageContentsList) bdy;

                    try {
                        projectDao.deleteProject((long) queryData.get(0));
                        return Response.ok().build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }
                });


        from("direct:findProjectById").routeId("findProjectById")
                .log("${headers}")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList queryData = (MessageContentsList) bdy;

                    try {
                        var prj = projectDao.findProjectById((long) queryData.get(0));
                        return Response.ok(prj).build();
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



