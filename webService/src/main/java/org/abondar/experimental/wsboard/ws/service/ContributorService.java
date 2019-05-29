package org.abondar.experimental.wsboard.ws.service;

import javax.ws.rs.core.Response;

/**
 * Contributor web service
 */
public interface ContributorService extends RestService {

    Response createContributor(long userId, long projectId, boolean isOwner);

    Response updateContributor(long contributorId, Boolean isOwner, Boolean isActive);

    Response findProjectOwner(long projectId);

    Response findProjectContributors(long projectId, int offset, int limit);
}
