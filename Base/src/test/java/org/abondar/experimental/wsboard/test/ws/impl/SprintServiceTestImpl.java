package org.abondar.experimental.wsboard.test.ws.impl;

import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.Sprint;
import org.abondar.experimental.wsboard.ws.service.SprintService;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test implementation of sprint web service
 */
@Path("/sprint")
public class SprintServiceTestImpl implements SprintService {


    private Project testProject;
    private Sprint testSprint;

    @Override
    public Response createSprint(String name, String startDate, String endDate,long projectId) {

        var existingSprint = new Sprint("exist", new Date(), new Date(),projectId);

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

            if (testProject.getId()!=projectId){
                return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
            }

            testSprint = new Sprint(name, startDt, endDt,projectId);
            testSprint.setId(10);

            return Response.ok(testSprint).build();
        } catch (DataCreationException ex) {
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
        }

    }

    @Override
    public Response updateSprint(long sprintId, String name, String startDate, String endDate, Boolean isCurrent) {

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

        if (isCurrent!=null){
            if (isCurrent && testSprint.isCurrent()){
                return Response.status(Response.Status.CONFLICT).entity(LogMessageUtil.SPRINT_ACTIVE_EXISTS).build();
            }

            testSprint.setCurrent(isCurrent);
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
    public Response getCurrentSprint(long projectId) {
        if (testProject.getId()!=projectId){
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        if (!testSprint.isCurrent()){
            return Response.status(Response.Status.NO_CONTENT).build();
        }


        return Response.ok(testSprint).build();
    }

    @Override
    public Response getSprints(long projectId,int offset, int limit) {
        if (testProject.getId()!=projectId){
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        var sprints = List.of(testSprint, new Sprint(), new Sprint(), new Sprint(), new Sprint());

        if (offset == -1) {
            return Response.ok(sprints).build();
        }

        sprints = sprints.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        if (sprints.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.ok(sprints).build();
    }

    @Override
    public Response countSprints(long projectId) {
        return Response.ok(7).build();
    }

    @Override
    public Response deleteSprint(long sprintId) {
        if (testSprint.getId() != sprintId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.SPRINT_NOT_EXISTS).build();
        }

        return Response.ok().build();
    }


    @POST
    @Path("/project")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProject(@FormParam("name") String name,
                                  @FormParam("startDate") String startDate) {


        Date stDate;
        try {
            stDate = convertDate(startDate);
        } catch (DataCreationException ex) {
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(LogMessageUtil.PARSE_DATE_FAILED).build();
        }

        testProject = new Project(name, stDate);
        testProject.setId(10);

        return Response.ok(testProject).build();
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
