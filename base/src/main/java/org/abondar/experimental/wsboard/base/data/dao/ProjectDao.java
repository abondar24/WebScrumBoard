package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.base.data.ObjectWrapper;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


public class ProjectDao {

    private static Logger logger = LoggerFactory.getLogger(ProjectDao.class);

    private DataMapper mapper;

    public ProjectDao(DataMapper mapper) {
        this.mapper = mapper;
    }


    public ObjectWrapper<Project> createProject(String name, Date startDate) {
        ObjectWrapper<Project> res = new ObjectWrapper<>();
        var prj = mapper.getProjectByName(name);

        if (prj != null) {
            logger.error(ErrorMessageUtil.PROJECT_EXISTS);
            res.setMessage(ErrorMessageUtil.PROJECT_EXISTS);

            return res;
        }

        prj = new Project(name, startDate);
        mapper.insertUpdateProject(prj);

        logger.info("Project successfully created with id: " + prj.getId());
        res.setObject(prj);

        return res;
    }

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

        if (repo!=null && !repo.isBlank()){
            prj.setName(name);
        }

        if (isActive!=null){
            prj.setActive(isActive);
            if (!isActive){
                if (endDate!=null && !prj.getStartDate().after(endDate)){
                    prj.setEndDate(endDate);
                } else {
                    res.setMessage(ErrorMessageUtil.WRONG_END_DATE);
                    return res;
                }
            }
        }

        mapper.insertUpdateProject(prj);
        logger.info("Project successfully updated");

        res.setObject(prj);
        return res;
    }

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
