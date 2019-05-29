package org.abondar.experimental.wsboard.ws.impl;

import org.abondar.experimental.wsboard.dao.ContributorDao;
import org.abondar.experimental.wsboard.ws.service.ContributorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
 * Contributor service implementation
 */
@Path("/contributor")
public class ContributorServiceImpl implements ContributorService {

    @Autowired
    @Qualifier("contributorDao")
    private ContributorDao contributorDao;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response createContributor(@FormParam("userId") long userId,
                                      @FormParam("projectId") long projectId,
                                      @FormParam("isOwner") boolean isOwner) {


        return null;
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateContributor(@FormParam("ctrId") long contributorId,
                                      @FormParam("isOwner") Boolean isOwner,
                                      @FormParam("isActive") Boolean isActive) {
        return null;
    }

    @GET
    @Path("/find_project_owner")
    @Override
    public Response findProjectOwner(@QueryParam("projectId") long projectId) {
        return null;
    }

    @GET
    @Path("/find_project_contributors")
    @Override
    public Response findProjectContributors(@QueryParam("projectId") long projectId,
                                            @QueryParam("offset") int offset,
                                            @QueryParam("limit") int limit) {
        return null;
    }
}
