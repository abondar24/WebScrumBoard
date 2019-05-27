package org.abondar.experimental.wsboard.test.ws.impl;

import org.abondar.experimental.wsboard.ws.service.SprintService;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Test implementation of sprint web service
 */
@Path("/sprint")
public class SprintServiceTestImpl implements SprintService {


    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response createSprint(@FormParam("name") String name,
                                 @FormParam("startDate") String startDate,
                                 @FormParam("endDate") String endDate) {

        return null;
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateSprint(@FormParam("id") long sprintId,
                                 @FormParam("name") String name,
                                 @FormParam("startDate") String startDate,
                                 @FormParam("endDate") String endDate) {
        return null;
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getSprintById(@QueryParam("id") long sprintId) {
        return null;
    }

    @GET
    @Path("/find_all")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getSprints(@QueryParam("offset") int offset, @QueryParam("limit") int limit) {
        return null;
    }

    @GET
    @Path("/delete")
    @Override
    public Response deleteSprint(@QueryParam("id") long sprintId) {
        return null;
    }
}
