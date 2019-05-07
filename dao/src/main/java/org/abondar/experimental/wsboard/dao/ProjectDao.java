package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.dao.data.ObjectWrapper;
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
     * @return Object wrapper with project pojo or with error message
     */
    public ObjectWrapper<Project> createProject(String name, Date startDate) {
        ObjectWrapper<Project> res = new ObjectWrapper<>();
        var prj = mapper.getProjectByName(name);

        if (prj != null) {
            logger.error(ErrorMessageUtil.PROJECT_EXISTS);
            res.setMessage(ErrorMessageUtil.PROJECT_EXISTS);

            return res;
        }

        prj = new Project(name, startDate);
        mapper.insertProject(prj);

        logger.info("Project successfully created with id: " + prj.getId());
        res.setObject(prj);

        return res;
    }

    /**
     * Update an existing project
     *
     * @param id       - project id
     * @param name     - project name
     * @param repo     - project git repository
     * @param isActive - project currently active
     * @param endDate  - project end date
     * @return Object wrapper with project pojo or with error message
     */
    public ObjectWrapper<Project> updateProject(Long id, String name, String repo,
                                                Boolean isActive, Date endDate) {
        ObjectWrapper<Project> res = new ObjectWrapper<>();
        var prj = mapper.getProjectById(id);

        if (prj == null) {
            logger.error(ErrorMessageUtil.PROJECT_NOT_EXISTS);
            res.setMessage(ErrorMessageUtil.PROJECT_NOT_EXISTS);
            return res;
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
                    res.setMessage(ErrorMessageUtil.PROJECT_WRONG_END_DATE);
                    return res;
                }
            }
        }

        mapper.updateProject(prj);
        logger.info("Project successfully updated");

        res.setObject(prj);
        return res;
    }

    /**
     * Delete a project
     *
     * @param id - project id
     * @return Object wrapper with project id or with error message
     */
    public ObjectWrapper<Long> deleteProject(Long id) {
        ObjectWrapper<Long> res = new ObjectWrapper<>();
        var prj = mapper.getProjectById(id);

        if (prj == null) {
            logger.error(ErrorMessageUtil.PROJECT_NOT_EXISTS);
            res.setMessage(ErrorMessageUtil.PROJECT_NOT_EXISTS);

            return res;
        }

        mapper.deleteProject(id);
        logger.info("Project with id: " + id + " successfully updated");
        res.setObject(id);
        return res;
    }


    /**
     * Find a project by id
     *
     * @param id - project id
     * @return Object wrapper with project pojo or with error message
     */
    public ObjectWrapper<Project> findProjectById(long id) {
        ObjectWrapper<Project> res = new ObjectWrapper<>();
        var prj = mapper.getProjectById(id);

        if (prj == null) {
            logger.error(ErrorMessageUtil.PROJECT_NOT_EXISTS + "with id:" + id);
            res.setMessage(ErrorMessageUtil.PROJECT_NOT_EXISTS);

            return res;
        }

        logger.info("Project found with id: " + prj.getId());
        res.setObject(prj);

        return res;
    }

}
