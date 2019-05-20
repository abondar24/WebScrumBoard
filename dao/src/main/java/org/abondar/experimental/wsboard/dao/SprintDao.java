package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Sprint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import static org.abondar.experimental.wsboard.dao.data.LogMessageUtil.LOG_FORMAT;

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
     * @return sprint POJO
     */
    public Sprint createSprint(String name, Date startDate, Date endDate) throws DataExistenceException {


        var sprint = mapper.getSprintByName(name);
        if (sprint != null) {
            logger.error(LogMessageUtil.SPRINT_EXISTS);
            throw new DataExistenceException(LogMessageUtil.SPRINT_EXISTS);

        }

        sprint = new Sprint(name, startDate, endDate);

        mapper.insertSprint(sprint);

        var msg = String.format(LOG_FORMAT, "Created sprint ", sprint.getId());
        logger.info(msg);

        return sprint;
    }

    /**
     * Update existing sprint
     *
     * @param sprintId  - sprint id
     * @param name      - sprint name
     * @param startDate - sprint start date
     * @param endDate   - sprint end date
     * @return sprint POJO
     */
    public Sprint updateSprint(long sprintId, String name, Date startDate, Date endDate) throws DataExistenceException {

        var sprint = mapper.getSprintById(sprintId);
        if (sprint == null) {
            logger.error(LogMessageUtil.SPRINT_NOT_EXISTS);
            throw new DataExistenceException(LogMessageUtil.SPRINT_NOT_EXISTS);
        }

        if (name != null && !name.isBlank()) {
            if (mapper.getSprintByName(name) != null) {
                logger.error(LogMessageUtil.SPRINT_EXISTS);
                throw new DataExistenceException(LogMessageUtil.SPRINT_EXISTS);

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

        var msg = String.format(LOG_FORMAT, "Updated sprint ", sprint.getId());
        logger.info(msg);

        return sprint;
    }

    /**
     * Find a sprint by id
     *
     * @param sprintId - sprint id
     * @return sprint POJO
     */
    public Sprint getSprintById(long sprintId) throws DataExistenceException {

        var sprint = mapper.getSprintById(sprintId);
        if (sprint == null) {
            logger.error(LogMessageUtil.SPRINT_NOT_EXISTS);
            throw new DataExistenceException(LogMessageUtil.SPRINT_NOT_EXISTS);

        }

        var msg = String.format(LOG_FORMAT, "Found sprint ", sprint.getId());
        logger.info(msg);

        return sprint;
    }

    /**
     * Get the list of sprints with offset and limit
     *
     * @param offset - start of list
     * @param limit  - list size
     * @return Object wrapper with sprint POJO list or with error message
     */
    public List<Sprint> getSprints(int offset, int limit) {
        var sprints = mapper.getSprints(offset, limit);

        var msg = String.format("%s %d", "Found sprints: ", sprints.size());
        logger.info(msg);

        return sprints;
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
            logger.error(LogMessageUtil.SPRINT_NOT_EXISTS);
            return false;
        }

        mapper.deleteSprint(sprintId);

        var msg = String.format(LOG_FORMAT, "Deleted sprint ", sprint.getId());
        logger.info(msg);

        return true;
    }

}
