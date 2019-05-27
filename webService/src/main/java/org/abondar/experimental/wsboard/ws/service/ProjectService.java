package org.abondar.experimental.wsboard.ws.service;

import javax.ws.rs.core.Response;

/**
 * Project web service interface
 */
public interface ProjectService extends RestService {

    Response createProject(String name, String startDate);

    Response updateProject(long id, String name, String repo, String isActive, String endDate);

    Response deleteProject(long id);

    Response findProjectById(long id);
}
