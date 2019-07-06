package org.abondar.experimental.wsboard.ws.impl;


import org.abondar.experimental.wsboard.dao.ProjectDao;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.ws.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * Project web service implementation
 *
 * @author a.bondar
 */
public class ProjectServiceImpl implements ProjectService {


    private static Logger logger = LoggerFactory.getLogger(ProjectService.class);
    @Autowired
    @Qualifier("projectDao")
    private ProjectDao projectDao;


    @Override
    public Response createProject(String name, String startDate) {

        try {
            var prj = projectDao.createProject(name, convertDate(startDate));

            return Response.ok(prj).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
        } catch (DataCreationException ex) {
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
        }
    }


    @Override
    public Response updateProject(long id, String name, String repo, Boolean isActive, String endDate) {

        try {
            Date endDt = null;
            if (endDate != null) {
                endDt = convertDate(endDate);
            }


            var prj = projectDao.updateProject(id, name, repo, isActive, endDt);
            return Response.ok(prj).build();
        } catch (DataExistenceException ex) {
            if (ex.getMessage().equals(LogMessageUtil.PROJECT_NOT_EXISTS)) {
                return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
            } else {
                return Response.status(Response.Status.FOUND).entity(ex.getLocalizedMessage()).build();
            }

        } catch (DataCreationException ex) {
            switch (ex.getMessage()) {
                case LogMessageUtil.WRONG_END_DATE:
                    return Response.status(Response.Status.RESET_CONTENT).entity(ex.getLocalizedMessage()).build();
                case LogMessageUtil.PARSE_DATE_FAILED:
                    return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
                default:
                    return Response.status(Response.Status.MOVED_PERMANENTLY).entity(ex.getLocalizedMessage()).build();

            }

        }
    }

    @Override
    public Response deleteProject(long id) {
        try {
            projectDao.deleteProject(id);
            return Response.ok().build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }
    }


    @Override
    public Response findProjectById(long id) {
        try {
            var prj = projectDao.findProjectById(id);
            return Response.ok(prj).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getLocalizedMessage()).build();
        }

    }


}
