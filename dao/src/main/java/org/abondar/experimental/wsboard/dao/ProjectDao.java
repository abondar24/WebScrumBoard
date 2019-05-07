package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.data.ErrorMessageUtil;
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
     * @return project POJO
     */
    public Project createProject(String name, Date startDate) throws DataExistenceException {
        var prj = mapper.getProjectByName(name);

        if (prj != null) {
            logger.error(ErrorMessageUtil.PROJECT_EXISTS);
            throw new DataExistenceException(ErrorMessageUtil.PROJECT_EXISTS);

        }

        prj = new Project(name, startDate);
        mapper.insertProject(prj);

        logger.info("Project successfully created with id: " + prj.getId());
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
            logger.error(ErrorMessageUtil.PROJECT_NOT_EXISTS);
            throw new DataExistenceException(ErrorMessageUtil.PROJECT_NOT_EXISTS);
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
                    throw new DataCreationException(ErrorMessageUtil.PROJECT_WRONG_END_DATE);
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
            logger.error(ErrorMessageUtil.PROJECT_NOT_EXISTS);
            throw new DataExistenceException(ErrorMessageUtil.PROJECT_NOT_EXISTS);
        }

        mapper.deleteProject(id);
        logger.info("Project with id: " + id + " successfully updated");

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

        if (prj == null) {
            logger.error(ErrorMessageUtil.PROJECT_NOT_EXISTS + "with id:" + id);
            throw new DataExistenceException(ErrorMessageUtil.PROJECT_NOT_EXISTS);
        }

        logger.info("Project found with id: " + prj.getId());
        return prj;
    }

}
