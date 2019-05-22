package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;



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
     * @throws DataExistenceException - project not found
     * @throws DataCreationException - name or startDate are blank
     * @return project POJO
     */
    public Project createProject(String name, Date startDate) throws DataExistenceException,
            DataCreationException {
        var prj = mapper.getProjectByName(name);

        if (prj != null) {
            logger.error(LogMessageUtil.PROJECT_EXISTS);
            throw new DataExistenceException(LogMessageUtil.PROJECT_EXISTS);

        }

        if (name == null || name.isBlank()) {
            logger.error(LogMessageUtil.BLANK_DATA);
            throw new DataCreationException(LogMessageUtil.BLANK_DATA);
        }

        if (startDate == null) {
            logger.error(LogMessageUtil.BLANK_DATA);
            throw new DataCreationException(LogMessageUtil.BLANK_DATA);
        }

        prj = new Project(name, startDate);
        mapper.insertProject(prj);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Project successfully created ", prj.getId());
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
    public Project updateProject(long id, String name, String repo,
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

        if (isActive != null && (isActive != prj.isActive())) {
            if (isActive && !prj.isActive()) {
                throw new DataCreationException(LogMessageUtil.PROJECT_CANNOT_BE_REACTIVATED);
            }

            if (!isActive) {
                if (endDate != null && !prj.getStartDate().after(endDate)) {
                    prj.setEndDate(endDate);
                } else {
                    throw new DataCreationException(LogMessageUtil.PROJECT_WRONG_END_DATE);
                }
                }

            prj.setActive(isActive);

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
    public long deleteProject(long id) throws DataExistenceException {
        var prj = mapper.getProjectById(id);

        if (prj == null) {
            logger.error(LogMessageUtil.PROJECT_NOT_EXISTS);
            throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);
        }

        mapper.deleteProject(id);

        var msg = String.format(LogMessageUtil.LOG_FORMAT + " %s", "Project ", id, " successfully updated");
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
            msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.PROJECT_NOT_EXISTS, id);
            logger.error(msg);
            throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);
        }

        msg = String.format(LogMessageUtil.LOG_FORMAT, "Project found ", prj.getId());
        logger.info(msg);
        return prj;
    }

}
