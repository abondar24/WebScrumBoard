package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.dao.data.ObjectWrapper;
import org.abondar.experimental.wsboard.datamodel.Sprint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Data access object for sprint
 *
 * @author a.bondar
 */
public class SprintDao extends BaseDao {

    private static Logger logger = LoggerFactory.getLogger(SprintDao.class);

    public SprintDao(DataMapper mapper) {
        super(mapper);
    }


    /**
     * Create a new sprint
     *
     * @param name      - sprint name
     * @param startDate - sprint start date
     * @param endDate   - sprint end date
     * @return Object wrapper with sprint POJO or with error message
     */
    public ObjectWrapper<Sprint> createSprint(String name, Date startDate, Date endDate) {
        ObjectWrapper<Sprint> res = new ObjectWrapper<>();

        var sprint = mapper.getSprintByName(name);
        if (sprint != null) {
            logger.error(ErrorMessageUtil.SPRINT_EXISTS);
            res.setMessage(ErrorMessageUtil.SPRINT_EXISTS);

            return res;
        }

        sprint = new Sprint(name, startDate, endDate);

        mapper.insertSprint(sprint);
        logger.info("Created sprint with id: " + sprint.getId());

        res.setObject(sprint);
        return res;
    }

    /**
     * Update existing sprint
     *
     * @param sprintId  - sprint id
     * @param name      - sprint name
     * @param startDate - sprint start date
     * @param endDate   - sprint end date
     * @return Object wrapper with sprint POJO or with error message
     */
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

        mapper.updateSprint(sprint);
        logger.info("Updated sprint with id: " + sprint.getId());

        res.setObject(sprint);
        return res;
    }

    /**
     * Find a sprint by id
     *
     * @param sprintId - sprint id
     * @return Object wrapper with sprint POJO or with error message
     */
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

    /**
     * Get the list of sprints with offset and limit
     *
     * @param offset - start of list
     * @param limit  - list size
     * @return Object wrapper with sprint POJO list or with error message
     */
    public ObjectWrapper<List<Sprint>> getSprints(int offset, int limit) {
        ObjectWrapper<List<Sprint>> res = new ObjectWrapper<>();

        var sprints = mapper.getSprints(offset, limit);

        logger.info("Found sprints: " + sprints.size());

        res.setObject(sprints);
        return res;
    }

    /**
     * Delete a sprint
     *
     * @param sprintId - sprint id
     * @return true if sprint deleted, false if error
     */
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
