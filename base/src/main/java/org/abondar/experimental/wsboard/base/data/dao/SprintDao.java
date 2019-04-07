package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.base.data.ObjectWrapper;
import org.abondar.experimental.wsboard.base.data.event.EventPublisher;
import org.abondar.experimental.wsboard.datamodel.Sprint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class SprintDao extends BaseDao {

    private static Logger logger = LoggerFactory.getLogger(SprintDao.class);

    public SprintDao(DataMapper mapper, EventPublisher eventPublisher) {
        super(mapper, eventPublisher);
    }


    public ObjectWrapper<Sprint> createSprint(String name, Date startDate, Date endDate) {
        ObjectWrapper<Sprint> res = new ObjectWrapper<>();

        var sprint = mapper.getSprintByName(name);
        if (sprint != null) {
            logger.error(ErrorMessageUtil.SPRINT_EXISTS);
            res.setMessage(ErrorMessageUtil.SPRINT_EXISTS);

            return res;
        }

        sprint = new Sprint(name, startDate, endDate);

        mapper.insertUpdateSprint(sprint);
        logger.info("Created sprint with id: " + sprint.getId());

        res.setObject(sprint);
        return res;
    }

    public ObjectWrapper<Sprint> updateSprint(long sprintId, String name, Date startDate, Date endDate) {
        ObjectWrapper<Sprint> res = new ObjectWrapper<>();

        var sprint = mapper.getSprintById(sprintId);
        if (sprint == null) {
            logger.error(ErrorMessageUtil.SPRINT_NOT_EXISTS);
            res.setMessage(ErrorMessageUtil.SPRINT_NOT_EXISTS);

            return res;
        }

        if (name != null && !name.isBlank()) {
            if (mapper.getSprintByName(name) != null) {
                logger.error(ErrorMessageUtil.SPRINT_EXISTS);
                res.setMessage(ErrorMessageUtil.SPRINT_EXISTS);

                return res;
            }

            sprint.setName(name);
        }

        if (startDate != null) {
            sprint.setStartDate(startDate);
        }


        if (endDate != null) {
            sprint.setEndDate(startDate);
        }

        mapper.insertUpdateSprint(sprint);
        logger.info("Updated sprint with id: " + sprint.getId());

        res.setObject(sprint);
        return res;
    }

    public ObjectWrapper<Sprint> getSprintById(long sprintId) {
        ObjectWrapper<Sprint> res = new ObjectWrapper<>();

        var sprint = mapper.getSprintById(sprintId);
        if (sprint == null) {
            logger.error(ErrorMessageUtil.SPRINT_NOT_EXISTS);
            res.setMessage(ErrorMessageUtil.SPRINT_NOT_EXISTS);

            return res;
        }

        logger.info("Found sprint with id: " + sprint.getId());

        res.setObject(sprint);
        return res;
    }


    public ObjectWrapper<List<Sprint>> getSprints(int offset, int limit) {
        ObjectWrapper<List<Sprint>> res = new ObjectWrapper<>();

        var sprints = mapper.getSprints(offset, limit);

        logger.info("Found sprints: " + sprints.size());

        res.setObject(sprints);
        return res;
    }


    public boolean deleteSprint(long sprintId) {

        var sprint = mapper.getSprintById(sprintId);
        if (sprint == null) {
            logger.error(ErrorMessageUtil.SPRINT_NOT_EXISTS);
            return false;
        }

        mapper.deleteSprint(sprintId);
        logger.info("Deleted sprint with id: " + sprint.getId());

        return true;
    }

}
