package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static org.abondar.experimental.wsboard.dao.data.LogMessageUtil.LOG_FORMAT;

/**
 * Data access object for project
 *
 * @author a.bondar
 */
public class ProjectDao extends BaseDao {

    private static Logger logger = LoggerFactory.getLogger(ProjectDao.class);


    public ProjectDao(DataMapper mapper) {
        super(mapper);
    }


    /**
     * Create a new project
     *
     * @param name      - project name
     * @param startDate - project start date
     * @return project POJO
     */
    public Project createProject(String name, Date startDate) throws DataExistenceException {
        var prj = mapper.getProjectByName(name);

        if (prj != null) {
            logger.error(LogMessageUtil.PROJECT_EXISTS);
            throw new DataExistenceException(LogMessageUtil.PROJECT_EXISTS);

        }

        prj = new Project(name, startDate);
        mapper.insertProject(prj);

        var msg = String.format(LOG_FORMAT, "Project successfully created ", prj.getId());
        logger.info(msg);
        return prj;
    }

    /**
     * Update an existing project
     *
     * @param id       - project id
     * @param name     - project name
     * @param repo     - project git repository
     * @param isActive - project currently active
     * @param endDate  - project end date
     * @return project POJO
     */
    public Project updateProject(Long id, String name, String repo,
                                 Boolean isActive, Date endDate)
            throws DataExistenceException, DataCreationException {
        var prj = mapper.getProjectById(id);

        if (prj == null) {
            logger.error(LogMessageUtil.PROJECT_NOT_EXISTS);
            throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);
        }

        if (name != null && !name.isBlank()) {
            prj.setName(name);
        }

        if (repo != null && !repo.isBlank()) {
            prj.setName(name);
        }

        if (isActive != null) {
            prj.setActive(isActive);
            if (!isActive) {
                if (endDate != null && !prj.getStartDate().after(endDate)) {
                    prj.setEndDate(endDate);
                } else {
                    throw new DataCreationException(LogMessageUtil.PROJECT_WRONG_END_DATE);
                }
            }
        }

        mapper.updateProject(prj);
        logger.info("Project successfully updated");

        return prj;
    }

    /**
     * Delete a project
     *
     * @param id - project id
     * @return project id
     */
    public Long deleteProject(Long id) throws DataExistenceException {
        var prj = mapper.getProjectById(id);

        if (prj == null) {
            logger.error(LogMessageUtil.PROJECT_NOT_EXISTS);
            throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);
        }

        mapper.deleteProject(id);

        var msg = String.format(LOG_FORMAT + " %s", "Project ", id, " successfully updated");
        logger.info(msg);

        return id;
    }


    /**
     * Find a project by id
     *
     * @param id - project id
     * @return Object wrapper with project pojo or with error message
     */
    public Project findProjectById(long id) throws DataExistenceException {
        var prj = mapper.getProjectById(id);

        var msg = "";
        if (prj == null) {
            msg = String.format(LOG_FORMAT, LogMessageUtil.PROJECT_NOT_EXISTS, id);
            logger.error(msg);
            throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);
        }

        msg = String.format(LOG_FORMAT, "Project found ", prj.getId());
        logger.info(msg);
        return prj;
    }

}
