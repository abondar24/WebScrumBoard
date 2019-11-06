package org.abondar.experimental.wsboard.ws.route;


import org.abondar.experimental.wsboard.dao.ContributorDao;
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
import java.util.Locale;

import static org.abondar.experimental.wsboard.ws.util.RouteConstantUtil.LOG_HEADERS;

/**
 * Route for contributor service events
 *
 * @author a.bondar
 */
public class ContributorServiceRoute extends RouteBuilder {


    @Autowired
    @Qualifier("contributorDao")
    private ContributorDao contributorDao;

    @Autowired
    private MessageSource messageSource;


    @Override
    public void configure() throws Exception {

        from("direct:createContributor").routeId("createContributor")
                .log(LoggingLevel.DEBUG,LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList formData = (MessageContentsList) bdy;

                    try {
                        var ctr = contributorDao.createContributor((long) formData.get(0), (long) formData.get(1),
                                (boolean) formData.get(2));
                        return Response.ok(ctr).build();
                    } catch (DataExistenceException ex) {
                        if(ex.getMessage().equals(LogMessageUtil.CONTRIBUTOR_EXISTS_LOG)){
                            return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
                        } else {
                            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                        }

                    } catch (DataCreationException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.PROJECT_NOT_ACTIVE)) {
                            return Response.status(Response.Status.MOVED_PERMANENTLY).entity(ex.getLocalizedMessage()).build();
                        } else {
                            return Response.status(Response.Status.CONFLICT).entity(ex.getLocalizedMessage()).build();
                        }
                    }

                });

        from("direct:updateContributor").routeId("updateContributor")
                .log(LoggingLevel.DEBUG,LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList formData = (MessageContentsList) bdy;

                    try {

                        var ctr = contributorDao.updateContributor((long) formData.get(0),(long) formData.get(1), (boolean) formData.get(2),
                                (boolean) formData.get(3));
                        return Response.ok(ctr).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    } catch (DataCreationException ex) {
                        switch (ex.getMessage()) {
                            case LogMessageUtil.CONTRIBUTOR_IS_ALREADY_OWNER:
                                return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
                            case LogMessageUtil.PROJECT_HAS_NO_OWNER:
                                return Response.status(Response.Status.CONFLICT).entity(ex.getLocalizedMessage()).build();
                            case LogMessageUtil.CONTRIBUTOR_NOT_ACTIVE:
                                return Response.status(Response.Status.GONE).entity(ex.getLocalizedMessage()).build();
                            case LogMessageUtil.CONTRIBUTOR_CANNOT_BE_DEACTIVATED:
                                return Response.status(Response.Status.FORBIDDEN).entity(ex.getLocalizedMessage()).build();
                            default:
                                return Response.status(Response.Status.FORBIDDEN).build();
                        }

                    }
                });

        from("direct:findProjectOwner").routeId("findProjectOwner")
                .log(LoggingLevel.DEBUG,LOG_HEADERS)
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
                .log(LoggingLevel.DEBUG,LOG_HEADERS)
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

        from("direct:countProjectContributors").routeId("countProjectContributors")
                .log(LoggingLevel.DEBUG,LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList queryData = (MessageContentsList) bdy;
                    try {
                        var contributors = contributorDao.countProjectContributors((long) queryData.get(0));

                        return Response.ok(contributors).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }


                });


        from("direct:findContributorsByUserId").routeId("findContributorsByUserId")
                .log(LoggingLevel.DEBUG,LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList queryData = (MessageContentsList) bdy;
                    try {
                        var contributors = contributorDao.findContributorsByUserId((long) queryData.get(0),
                                (int) queryData.get(1), (int) queryData.get(2));
                        if (contributors.isEmpty()) {
                            return Response.status(Response.Status.NO_CONTENT).build();
                        }

                        return Response.ok(contributors).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }

                });

        from("direct:findProjectContributor").routeId("findProjectContributor")
                .log(LoggingLevel.DEBUG,LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {

                    MessageContentsList queryData = (MessageContentsList) bdy;
                    try {
                        var res = contributorDao.findContributorByUserAndProject((long) queryData.get(0),
                                (long) queryData.get(1));

                        return Response.ok(res.orElse(0L)).build();
                    } catch (DataExistenceException ex) {
                        return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
                    }


                });
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
