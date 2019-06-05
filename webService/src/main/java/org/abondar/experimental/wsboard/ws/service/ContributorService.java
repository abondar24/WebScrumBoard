package org.abondar.experimental.wsboard.ws.service;

import javax.ws.rs.core.Response;

/**
 * Contributor CRUD web service
 */
public interface ContributorService extends RestService {

    Response createContributor(long userId, long projectId, String isOwner);

    Response updateContributor(long contributorId, String isOwner, String isActive);

    Response findProjectOwner(long projectId);

    Response findProjectContributors(long projectId, int offset, int limit);
}
