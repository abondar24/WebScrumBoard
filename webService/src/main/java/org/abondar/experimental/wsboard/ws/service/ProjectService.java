package org.abondar.experimental.wsboard.ws.service;

import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * Project web service interface
 */
public interface ProjectService extends RestService {

    Response createProject(String name, Date startDate);

    Response updateProject(Long id, String name, String repo, Boolean isActive, Date endDate);

    Response deleteProject(Long id);

    Response findProjectById(long id);
}
