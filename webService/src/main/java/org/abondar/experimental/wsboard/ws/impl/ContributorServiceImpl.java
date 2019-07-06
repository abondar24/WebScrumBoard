package org.abondar.experimental.wsboard.ws.impl;

import org.abondar.experimental.wsboard.dao.ContributorDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.ws.service.ContributorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.core.Response;

/**
 * Contributor service implementation
 *
 * @author a.bondar
 */
public class ContributorServiceImpl implements ContributorService {

    private static Logger logger = LoggerFactory.getLogger(ContributorService.class);

    @Autowired
    @Qualifier("contributorDao")
    private ContributorDao contributorDao;


    @Override
    public Response createContributor(long userId, long projectId, boolean isOwner) {

        try {
            var ctr = contributorDao.createContributor(userId, projectId, Boolean.valueOf(isOwner));
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

    }


    @Override
    public Response updateContributor(long contributorId, Boolean isOwner, Boolean isActive) {

        try {

            var ctr = contributorDao.updateContributor(contributorId, isOwner, isActive);
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
    }


    @Override
    public Response findProjectOwner(long projectId) {

        try {
            var owner = contributorDao.findProjectOwner(projectId);
            return Response.ok(owner).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ex) {
            return Response.status(Response.Status.NO_CONTENT).entity(ex.getLocalizedMessage()).build();
        }

    }


    @Override
    public Response findProjectContributors(long projectId, int offset, int limit) {

        try {
            var contributors = contributorDao.findProjectContributors(projectId, offset, limit);
            if (contributors.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            return Response.ok(contributors).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }
}
