package org.abondar.experimental.wsboard.test.ws.impl;

import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.datamodel.Sprint;
import org.abondar.experimental.wsboard.ws.service.SprintService;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test implementation of sprint web service
 */
@Path("/sprint")
public class SprintServiceTestImpl implements SprintService {


    private Sprint testSprint;

    @Override
    public Response createSprint(String name, String startDate, String endDate) {

        var existingSprint = new Sprint("exist", new Date(), new Date());

        if (existingSprint.getName().equals(name)) {
            return Response.status(Response.Status.FOUND).entity(LogMessageUtil.SPRINT_EXISTS).build();
        }


        try {
            var startDt = convertDate(startDate);
            var endDt = convertDate(endDate);

            if (name == null || name.isBlank()) {
                return Response.status(Response.Status.RESET_CONTENT)
                        .entity(LogMessageUtil.BLANK_DATA).build();
            }


            if (startDt.after(endDt)) {
                return Response.status(Response.Status.RESET_CONTENT).entity(LogMessageUtil.WRONG_END_DATE).build();

            }

            testSprint = new Sprint(name, startDt, endDt);
            testSprint.setId(10);

            return Response.ok(testSprint).build();
        } catch (DataCreationException ex) {
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
        }

    }

    @Override
    public Response updateSprint(long sprintId, String name, String startDate, String endDate) {

        if (testSprint.getId() != sprintId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.SPRINT_NOT_EXISTS).build();
        }

        if (name != null && !name.isBlank()) {
            if (testSprint.getName().equals(name)) {
                return Response.status(Response.Status.FOUND).entity(LogMessageUtil.SPRINT_EXISTS).build();
            } else {
                testSprint.setName(name);
            }
        }

        try {
            Date startDt;
            if (startDate != null && !startDate.isBlank()) {
                startDt = convertDate(startDate);
                testSprint.setStartDate(startDt);

            }

            Date endDt;
            if (endDate != null && !endDate.isBlank()) {
                endDt = convertDate(endDate);
                if (testSprint.getStartDate().after(endDt)) {
                    return Response.status(Response.Status.RESET_CONTENT).entity(LogMessageUtil.WRONG_END_DATE).build();
                }
                testSprint.setEndDate(endDt);
            }


            return Response.ok(testSprint).build();
        } catch (DataCreationException ex) {
                return Response.status(Response.Status.NO_CONTENT).entity(ex.getLocalizedMessage()).build();
        }

    }

    @Override
    public Response getSprintById(long sprintId) {
        if (testSprint.getId() != sprintId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.SPRINT_NOT_EXISTS).build();
        }

        return Response.ok(testSprint).build();
    }

    @Override
    public Response getSprints(int offset, int limit) {
        var sprints = List.of(testSprint, new Sprint(), new Sprint(), new Sprint(), new Sprint());

        if (offset == -1) {
            return Response.ok(sprints).build();
        }


        sprints = sprints.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        if (sprints.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(sprints).build();
    }

    @Override
    public Response deleteSprint(long sprintId) {
        if (testSprint.getId() != sprintId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.SPRINT_NOT_EXISTS).build();
        }

        return Response.ok().build();
    }
}
