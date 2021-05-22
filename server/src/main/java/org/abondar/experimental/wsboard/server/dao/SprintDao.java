package org.abondar.experimental.wsboard.server.dao;

import org.abondar.experimental.wsboard.server.datamodel.Sprint;
import org.abondar.experimental.wsboard.server.exception.DataCreationException;
import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.abondar.experimental.wsboard.server.mapper.ProjectMapper;
import org.abondar.experimental.wsboard.server.mapper.SprintMapper;
import org.abondar.experimental.wsboard.server.util.LogMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


/**
 * Data access object for sprint
 *
 * @author a.bondar
 */
@Component
public class SprintDao{

    private static final Logger logger = LoggerFactory.getLogger(SprintDao.class);

    private final ProjectMapper projectMapper;

    private final SprintMapper sprintMapper;

    @Autowired
    public SprintDao(ProjectMapper projectMapper, SprintMapper sprintMapper) {
        this.projectMapper = projectMapper;
        this.sprintMapper = sprintMapper;
    }

    /**
     * Create a new sprint
     *
     * @param name      - sprint name
     * @param startDate - sprint start date
     * @param endDate   - sprint end date
     * @param projectId - project linked to sprint
     * @return sprint POJO
     * @throws DataExistenceException sprint with such name exists
     * @throws DataCreationException  sprint end date is before start date or blank data
     */
    public Sprint createSprint(String name, Date startDate, Date endDate, long projectId) throws DataExistenceException, DataCreationException {


        checkSprintByName(name);

        if (projectMapper.getProjectById(projectId) == null) {
            logger.error(LogMessageUtil.PROJECT_NOT_EXISTS);
            throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);
        }

        if (name == null || name.isBlank()) {
            logger.error(LogMessageUtil.BLANK_DATA);
            throw new DataCreationException(LogMessageUtil.BLANK_DATA);
        }


        if (startDate.after(endDate)) {
            logger.error(LogMessageUtil.WRONG_END_DATE);
            throw new DataCreationException(LogMessageUtil.WRONG_END_DATE);
        }

        var sprint = new Sprint(name, startDate, endDate, projectId);

        sprintMapper.insertSprint(sprint);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Created sprint ", sprint.getId());
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
     * @param isCurrent - sprint is current
     * @return sprint POJO
     * @throws DataExistenceException - sprint not exists or sprint name already exists,
     *                                there is already a current sprint for project
     * @throws DataCreationException  - sprint end date is before start date
     */
    public Sprint updateSprint(long sprintId, String name, Date startDate, Date endDate, Boolean isCurrent)
            throws DataExistenceException, DataCreationException {

        var sprint = getSprintById(sprintId);

        if (name != null && !name.isBlank()) {
            if (sprintMapper.getSprintByName(name) != null) {
                logger.error(LogMessageUtil.SPRINT_EXISTS);
                throw new DataExistenceException(LogMessageUtil.SPRINT_EXISTS);

            }

            sprint.setName(name);
        }

        if (startDate != null) {
            sprint.setStartDate(startDate);
        }


        if (endDate != null) {
            if (sprint.getStartDate().after(endDate)) {
                logger.error(LogMessageUtil.WRONG_END_DATE);
                throw new DataCreationException(LogMessageUtil.WRONG_END_DATE);
            }

            sprint.setEndDate(endDate);
        }

        if (isCurrent != null) {
            if (isCurrent && sprintMapper.getCurrentSprint(sprint.getProjectId()) != null) {
                throw new DataExistenceException(LogMessageUtil.SPRINT_ACTIVE_EXISTS);
            }

            sprint.setCurrent(isCurrent);
        }

        sprintMapper.updateSprint(sprint);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Updated sprint ", sprint.getId());
        logger.info(msg);

        return sprint;
    }

    /**
     * Find a sprint by id
     *
     * @param sprintId - sprint id
     * @return sprint POJO
     * @throws DataExistenceException - sprint not exists
     */
    public Sprint getSprintById(long sprintId) throws DataExistenceException {

        var sprint = sprintMapper.getSprintById(sprintId);
        if (sprint == null) {
            logger.error(LogMessageUtil.SPRINT_NOT_EXISTS);
            throw new DataExistenceException(LogMessageUtil.SPRINT_NOT_EXISTS);

        }

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Found sprint ", sprint.getId());
        logger.info(msg);

        return sprint;
    }

    /**
     * Get current sprint for project
     *
     * @param projectId - project id to look for
     * @return current sprint of project
     * @throws DataExistenceException - project not found
     */
    public Sprint getCurrentSprint(long projectId) throws DataExistenceException {

        checkProject(projectId);

        var sprint = sprintMapper.getCurrentSprint(projectId);
        if (sprint == null) {
            logger.info("Project has no active sprints");
            return null;
        } else {
            var msg = String.format(LogMessageUtil.LOG_FORMAT, "Found sprint ", sprint.getId());
            logger.info(msg);
            return sprint;
        }

    }

    /**
     * Get the list of sprints with offset and limit
     *
     * @param projectId - project for which sprints to retrieve
     * @param offset    - start of list
     * @param limit     - list size
     * @return Object wrapper with sprint POJO list or with error message
     */
    public List<Sprint> getSprints(long projectId, int offset, Integer limit) throws DataExistenceException {

        checkProject(projectId);

        var sprints = sprintMapper.getSprints(projectId, offset, limit);

        var msg = String.format("%s %d", "Found sprints: ", sprints.size());
        logger.info(msg);

        return sprints;
    }

    /**
     * Counts the number of sprints for project
     *
     * @param prjId - project to be counted
     * @return number of sprint tasks
     * @throws DataExistenceException - user not found
     */
    public Integer countSprints(Long prjId) throws DataExistenceException {

        checkProject(prjId);

        var res = sprintMapper.countSprints(prjId);
        var msg = String.format(LogMessageUtil.LOG_COUNT_FORMAT, "Counted tasks for user ", prjId, res);
        logger.info(msg);

        return res;

    }

    /**
     * Delete a sprint
     *
     * @param sprintId - sprint id
     */
    public void deleteSprint(long sprintId) throws DataExistenceException {

        var sprint = getSprintById(sprintId);

        sprintMapper.deleteSprint(sprintId);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Deleted sprint ", sprint.getId());
        logger.info(msg);

    }

    /**
     * Check if project exists
     * @param prjId - project id
     * @throws DataExistenceException - project not found
     */
    private void checkProject(long prjId) throws DataExistenceException {
        if (projectMapper.getProjectById(prjId) == null) {
            var msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.PROJECT_NOT_EXISTS, prjId);
            logger.error(msg);
            throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);
        }
    }

    /**
     * Check sprint exists
     * @param name - sprint name
     * @throws DataExistenceException - sprint not found
     */
    private void checkSprintByName(String name) throws DataExistenceException {
        if (sprintMapper.getSprintByName(name) != null) {
            logger.error(LogMessageUtil.SPRINT_EXISTS);
            throw new DataExistenceException(LogMessageUtil.SPRINT_EXISTS);

        }

    }
}
