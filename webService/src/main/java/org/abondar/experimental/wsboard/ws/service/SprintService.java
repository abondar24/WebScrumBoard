package org.abondar.experimental.wsboard.ws.service;

import javax.ws.rs.core.Response;

/**
 * Sprint CRUD web service
 */
public interface SprintService extends RestService {

    Response createSprint(String name, String startDate, String endDate);

    Response updateSprint(long sprintId, String name, String startDate, String endDate);

    Response getSprintById(long sprintId);

    Response getSprints(int offset, int limit);

    Response deleteSprint(long sprintId);

}
