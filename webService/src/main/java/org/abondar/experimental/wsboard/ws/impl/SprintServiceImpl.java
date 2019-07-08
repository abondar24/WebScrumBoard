package org.abondar.experimental.wsboard.ws.impl;


import org.abondar.experimental.wsboard.dao.SprintDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.ws.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * Sprint service implementation
 *
 * @author a.bondar
 */
public class SprintServiceImpl implements SprintService {


    @Autowired
    @Qualifier("sprintDao")
    private SprintDao sprintDao;


    @Override
    public Response createSprint(String name, String startDate, String endDate) {

        try {
            var startDt = convertDate(startDate);
            var endDt = convertDate(endDate);

            var sprint = sprintDao.createSprint(name, startDt, endDt);
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
    }


    @Override
    public Response updateSprint(long sprintId, String name, String startDate, String endDate) {
        try {
            Date startDt = null;
            if (!startDate.isBlank()) {
                startDt = convertDate(startDate);

            }

            Date endDt = null;
            if (!endDate.isBlank()) {
                endDt = convertDate(endDate);
            }

            var sprint = sprintDao.updateSprint(sprintId, name, startDt, endDt);

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
    }


    @Override
    public Response getSprintById(long sprintId) {
        try {
            var sprint = sprintDao.getSprintById(sprintId);
            return Response.ok(sprint).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }


    @Override
    public Response getSprints(int offset, int limit) {

        var sprints = sprintDao.getSprints(offset, limit);
        if (sprints.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(sprints).build();
    }


    @Override
    public Response deleteSprint(long sprintId) {
        try {
            sprintDao.deleteSprint(sprintId);
            return Response.ok().build();

        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }


}
