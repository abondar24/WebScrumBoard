package org.abondar.experimental.wsboard.route;


import org.abondar.experimental.wsboard.dao.ContributorDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.ws.util.I18nKeyUtil;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.context.MessageSource;

import javax.ws.rs.core.Response;
import java.util.Locale;

import static org.abondar.experimental.wsboard.ws.util.RouteConstantUtil.ACCEPT_LANG_HEADER;
import static org.abondar.experimental.wsboard.ws.util.RouteConstantUtil.LOG_HEADERS;

/**
 * Route for contributor service events
 *
 * @author a.bondar
 */
public class ContributorServiceRoute extends RouteBuilder {


    private final ContributorDao contributorDao;

    private final MessageSource messageSource;

    //@Autowired
    public ContributorServiceRoute(ContributorDao contributorDao, MessageSource messageSource) {
        this.contributorDao = contributorDao;
        this.messageSource = messageSource;
    }

    @Override
    public void configure() throws Exception {

        from("direct:createContributor").routeId("createContributor")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        var ctr = contributorDao.createContributor((long) formData.get(0), (long) formData.get(1),
                                (boolean) formData.get(2));
                        return Response.ok(ctr).build();
                    } catch (DataExistenceException ex) {
                        if(ex.getMessage().equals(LogMessageUtil.CONTRIBUTOR_EXISTS_LOG)){
                            return getLocalizedResponse(lang, I18nKeyUtil.CONTRIBUTOR_EXISTS,Response.Status.NOT_FOUND);
                        } else if (ex.getMessage().equals(LogMessageUtil.USER_NOT_EXISTS)){
                            return getLocalizedResponse(lang, I18nKeyUtil.USER_NOT_EXISTS,Response.Status.NOT_FOUND);
                        } else {
                            return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_NOT_EXISTS,Response.Status.NOT_FOUND);
                        }

                    } catch (DataCreationException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.PROJECT_NOT_ACTIVE)) {
                            return getLocalizedResponse(lang,
                                    I18nKeyUtil.PROJECT_NOT_ACTIVE,Response.Status.MOVED_PERMANENTLY);
                        } else {
                            return getLocalizedResponse(lang,
                                    I18nKeyUtil.CONTRIBUTOR_IS_ALREADY_OWNER,Response.Status.CONFLICT);
                        }
                    }

                });

        from("direct:updateContributor").routeId("updateContributor")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList formData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {

                        var ctr = contributorDao.updateContributor((long) formData.get(0),(long) formData.get(1), (boolean) formData.get(2),
                                (boolean) formData.get(3));
                        return Response.ok(ctr).build();
                    } catch (DataExistenceException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.USER_NOT_EXISTS)){
                            return getLocalizedResponse(lang, I18nKeyUtil.USER_NOT_EXISTS,Response.Status.NOT_FOUND);
                        } else if (ex.getMessage().equals(LogMessageUtil.PROJECT_NOT_EXISTS)){
                            return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_NOT_EXISTS,Response.Status.NOT_FOUND);
                        } else {
                            return getLocalizedResponse(lang, I18nKeyUtil.CONTRIBUTOR_NOT_EXISTS,Response.Status.NOT_FOUND);
                        }
                    } catch (DataCreationException ex) {
                        switch (ex.getMessage()) {
                            case LogMessageUtil.CONTRIBUTOR_IS_ALREADY_OWNER:
                                return getLocalizedResponse(lang,
                                        I18nKeyUtil.CONTRIBUTOR_IS_ALREADY_OWNER,Response.Status.CONFLICT);
                            case LogMessageUtil.PROJECT_HAS_NO_OWNER:
                                return getLocalizedResponse(lang,
                                        I18nKeyUtil.PROJECT_HAS_NO_OWNER,Response.Status.CONFLICT);
                            case LogMessageUtil.CONTRIBUTOR_NOT_ACTIVE:
                                return getLocalizedResponse(lang,
                                        I18nKeyUtil.CONTRIBUTOR_NOT_ACTIVE,Response.Status.GONE);
                            case LogMessageUtil.CONTRIBUTOR_CANNOT_BE_DEACTIVATED:
                                return getLocalizedResponse(lang,
                                        I18nKeyUtil.CONTRIBUTOR_CANNOT_BE_DEACTIVATED,Response.Status.FORBIDDEN);
                            default:
                                return Response.status(Response.Status.FORBIDDEN).build();
                        }

                    }
                });

        from("direct:findProjectOwner").routeId("findProjectOwner")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        var owner = contributorDao.findProjectOwner((long) queryData.get(0));
                        return Response.ok(owner).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_NOT_EXISTS,Response.Status.NOT_FOUND);
                    } catch (DataCreationException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_HAS_NO_OWNER,Response.Status.NO_CONTENT);
                    }


                });

        from("direct:findProjectContributors").routeId("findProjectContributors")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        var contributors = contributorDao.findProjectContributors((long) queryData.get(0),
                                (int) queryData.get(1), (int) queryData.get(2));
                        if (contributors.isEmpty()) {
                            return Response.status(Response.Status.NO_CONTENT).build();
                        }

                        return Response.ok(contributors).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_NOT_EXISTS,Response.Status.NOT_FOUND);
                    }


                });

        from("direct:countProjectContributors").routeId("countProjectContributors")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get(RouteConstantUtil.ACCEPT_LANG_HEADER);

                    try {
                        var contributors = contributorDao.countProjectContributors((long) queryData.get(0));

                        return Response.ok(contributors).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_NOT_EXISTS,Response.Status.NOT_FOUND);
                    }


                });


        from("direct:findContributorsByUserId").routeId("findContributorsByUserId")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get("Accept-Language");

                    try {
                        var contributors = contributorDao.findContributorsByUserId((long) queryData.get(0),
                                (int) queryData.get(1), (int) queryData.get(2));
                        if (contributors.isEmpty()) {
                            return Response.status(Response.Status.NO_CONTENT).build();
                        }

                        return Response.ok(contributors).build();
                    } catch (DataExistenceException ex) {
                        return getLocalizedResponse(lang, I18nKeyUtil.USER_NOT_EXISTS,Response.Status.NOT_FOUND);
                    }

                });

        from("direct:findProjectContributor").routeId("findProjectContributor")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get("Accept-Language");

                    try {
                        var res = contributorDao.findContributorByUserAndProject((long) queryData.get(0),
                                (long) queryData.get(1));

                        return Response.ok(res.orElse(0L)).build();
                    } catch (DataExistenceException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.USER_NOT_EXISTS)){
                            return getLocalizedResponse(lang, I18nKeyUtil.USER_NOT_EXISTS,Response.Status.NOT_FOUND);
                        } else {
                            return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_NOT_EXISTS,Response.Status.NOT_FOUND);
                        }
                    }


                });

        from("direct:findContributorByLogin").routeId("findContributorByLogin")
                .log(LoggingLevel.DEBUG, RouteConstantUtil.LOG_HEADERS)
                .transform()
                .body((bdy, hdrs) -> {
                    MessageContentsList queryData = (MessageContentsList) bdy;
                    String lang = (String) hdrs.get("Accept-Language");

                    try {
                        var res = contributorDao.findContributorByLogin((long) queryData.get(0), (String) queryData.get(1));

                        return Response.ok(res).build();
                    } catch (DataExistenceException ex) {
                        if (ex.getMessage().equals(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS)){
                            return getLocalizedResponse(lang, I18nKeyUtil.CONTRIBUTOR_NOT_EXISTS,Response.Status.NOT_FOUND);
                        } else {
                            return getLocalizedResponse(lang, I18nKeyUtil.PROJECT_NOT_EXISTS,Response.Status.NOT_FOUND);
                        }
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
