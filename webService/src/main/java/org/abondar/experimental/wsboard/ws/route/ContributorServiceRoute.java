package org.abondar.experimental.wsboard.ws.route;


import org.abondar.experimental.wsboard.dao.ContributorDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.core.Response;

/**
 * Route for contributor service events
 *
 * @author a.bondar
 */
public class ContributorServiceRoute extends RouteBuilder {


    @Autowired
    @Qualifier("contributorDao")
    private ContributorDao contributorDao;


    @Override
    public void configure() throws Exception {

        from("direct:createContributor").routeId("createContributor")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList formData = (MessageContentsList) bdy;

                    try {
                        var ctr = contributorDao.createContributor((long) formData.get(0), (long) formData.get(1),
                                (boolean) formData.get(2));
                        return Response.ok(ctr).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    } catch (DataCreationException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.PROJECT_NOT_ACTIVE)) {
                            return Response.status(Response.Status.MOVED_PERMANENTLY).entity(ex.getLocalizedMessage()).build();
                        } else {
                            return Response.status(Response.Status.CONFLICT).entity(ex.getLocalizedMessage()).build();
                        }
                    }

                });

        from("direct:updateContributor").routeId("updateContributor")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList formData = (MessageContentsList) bdy;

                    try {

                        var ctr = contributorDao.updateContributor((long) formData.get(0), (boolean) formData.get(1),
                                (boolean) formData.get(2));
                        return Response.ok(ctr).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    } catch (DataCreationException ex) {
                        switch (ex.getMessage()) {
                            case LogMessageUtil.PROJECT_HAS_OWNER:
                                return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
                            case LogMessageUtil.PROJECT_HAS_NO_OWNER:
                                return Response.status(Response.Status.CONFLICT).entity(ex.getLocalizedMessage()).build();
                            case LogMessageUtil.CONTRIBUTOR_NOT_ACTIVE:
                                return Response.status(Response.Status.GONE).entity(ex.getLocalizedMessage()).build();
                            case LogMessageUtil.CONTRIBUTOR_CANNOT_BE_DEACTIVATED:
                                return Response.status(Response.Status.FORBIDDEN).entity(ex.getLocalizedMessage()).build();
                        }

                    }
                    return null;
                });

        from("direct:findProjectOwner").routeId("findProjectOwner")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList queryData = (MessageContentsList) bdy;
                    try {
                        var owner = contributorDao.findProjectOwner((long) queryData.get(0));
                        return Response.ok(owner).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    } catch (DataCreationException ex) {
                        return Response.status(Response.Status.NO_CONTENT).entity(ex.getLocalizedMessage()).build();
                    }


                });

        from("direct:findProjectContributors").routeId("findProjectContributors")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList queryData = (MessageContentsList) bdy;
                    try {
                        var contributors = contributorDao.findProjectContributors((long) queryData.get(0),
                                (int) queryData.get(1), (int) queryData.get(2));
                        if (contributors.isEmpty()) {
                            return Response.status(Response.Status.NO_CONTENT).build();
                        }

                        return Response.ok(contributors).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }


                });


    }
}