package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.task.Task;
import org.abondar.experimental.wsboard.datamodel.task.TaskState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.abondar.experimental.wsboard.datamodel.user.UserRole.DEVELOPER;
import static org.abondar.experimental.wsboard.datamodel.user.UserRole.DEV_OPS;
import static org.abondar.experimental.wsboard.datamodel.user.UserRole.QA;

/**
 * Data access object for task
 *
 * @author a.bondar
 */
public class TaskDao extends BaseDao {

    private static Logger logger = LoggerFactory.getLogger(TaskDao.class);

    private static final String LOG_FORMAT = "%s with id: %d";


    private Map<TaskState, List<TaskState>> stateMoves;

    public TaskDao(DataMapper mapper) {
        super(mapper);

        this.stateMoves = initMoves();
    }

    /**
     * Create a new task
     *
     * @param contributorId - contributor id
     * @param startDate     - start date of task
     * @param devOpsEnabled - dev ops state to be ignored or not
     * @return task POJO
     * @throws DataExistenceException - contributor not exists or is not active
     * @throws DataCreationException  - task start date not set
     */
    public Task createTask(long contributorId, Date startDate, boolean devOpsEnabled)
            throws DataExistenceException, DataCreationException {

        var ctr = mapper.getContributorById(contributorId);
        if (ctr == null || !ctr.isActive()) {
            logger.error(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);
            throw new DataExistenceException(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);

        }

        if (startDate == null) {
            logger.error(ErrorMessageUtil.TASK_START_DATE_NOT_SET);
            throw new DataCreationException(ErrorMessageUtil.TASK_START_DATE_NOT_SET);
        }


        var task = new Task(contributorId, startDate, devOpsEnabled);


        mapper.insertTask(task);
        logger.info("Created a task with id: " + task.getId());

        return task;
    }


    /**
     * Create existing task
     *
     * @param taskId        - task id
     * @param contributorId - contributor id
     * @param devOpsEnabled - dev ops state to be ignored or not
     * @param storyPoints   - task story points
     * @return task POJO
     * @throws DataExistenceException - task or contributor does not exist or contributor is not active
     */
    public Task updateTask(long taskId, Long contributorId, Boolean devOpsEnabled, Integer storyPoints)
            throws DataExistenceException {

        var task = mapper.getTaskById(taskId);
        if (task == null) {
            logger.info(String.format(LOG_FORMAT, ErrorMessageUtil.TASK_NOT_EXISTS, taskId));
            throw new DataExistenceException(ErrorMessageUtil.TASK_NOT_EXISTS);

        }

        if (contributorId != null) {
            var ctr = mapper.getContributorById(contributorId);
            if (ctr == null || !ctr.isActive()) {
                logger.error(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);
                throw new DataExistenceException(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);
            }


            task.setContributorId(contributorId);

        }

        if (devOpsEnabled != null) {
            task.setDevOpsEnabled(devOpsEnabled);
        }

        if (storyPoints != null) {
            task.setStoryPoints(storyPoints);
        }


        mapper.updateTask(task);
        logger.info("Updated a task with id: " + task.getId());

        return task;
    }

    /**
     * Set a sprint to a task
     *
     * @param taskId   - task id
     * @param sprintId - sprint id
     * @return task POJO
     * @throws DataExistenceException - task or sprint doesn't exist
     */
    public Task updateTaskSprint(long taskId, long sprintId) throws DataExistenceException {
        var task = mapper.getTaskById(taskId);
        if (task == null) {
            logger.info(String.format(LOG_FORMAT, ErrorMessageUtil.TASK_NOT_EXISTS, taskId));
            throw new DataExistenceException(ErrorMessageUtil.TASK_NOT_EXISTS);
        }

        var sprint = mapper.getSprintById(sprintId);
        if (sprint == null) {
            logger.info(String.format(LOG_FORMAT, ErrorMessageUtil.SPRINT_NOT_EXISTS, sprintId));
            throw new DataExistenceException(ErrorMessageUtil.SPRINT_NOT_EXISTS);
        }

        mapper.updateTaskSprint(taskId, sprintId);
        logger.info("Updated task sprint for id: " + task.getId());
        task.setSprintId(sprintId);

        return task;
    }


    /**
     * Update state of task
     *
     * @param taskId - task id
     * @param state  - new state
     * @return task POJO
     * @throws DataExistenceException - task state unknown,task not exists
     * @throws DataCreationException  - task already completed,task already completed,wrong state after pause,
     *                                state devOps not enabled,task move not available
     */
    public Task updateTaskState(long taskId, String state)
            throws DataExistenceException, DataCreationException {

        TaskState taskState;
        try {
            taskState = TaskState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException ex) {
            logger.error(ex.getMessage());
            throw new DataExistenceException(ErrorMessageUtil.TASK_STATE_UNKNOWN);
        }

        var task = mapper.getTaskById(taskId);
        if (task == null) {
            logger.info(String.format(LOG_FORMAT, ErrorMessageUtil.TASK_NOT_EXISTS, taskId));
            throw new DataExistenceException(ErrorMessageUtil.TASK_NOT_EXISTS);
        }

        if (task.getTaskState() == TaskState.COMPLETED) {
            logger.info(ErrorMessageUtil.TASK_ALREADY_COMPLETED);
            throw new DataCreationException(ErrorMessageUtil.TASK_ALREADY_COMPLETED);
        }

        if (taskState == TaskState.CREATED) {
            logger.info(ErrorMessageUtil.TASK_ALREADY_CREATED);
            throw new DataCreationException(ErrorMessageUtil.TASK_ALREADY_CREATED);
        }


        if ((task.getTaskState() == TaskState.PAUSED) && (task.getPrevState() != taskState)) {
            logger.info(ErrorMessageUtil.TASK_WRONG_STATE_AFTER_PAUSE);
            throw new DataCreationException(ErrorMessageUtil.TASK_WRONG_STATE_AFTER_PAUSE);
        }

        if (taskState == TaskState.IN_DEPLOYMENT && !task.isDevOpsEnabled()) {
            logger.info(ErrorMessageUtil.TASK_DEV_OPS_NOT_ENABLED);
            throw new DataCreationException(ErrorMessageUtil.TASK_DEV_OPS_NOT_ENABLED);
        }


        var ctr = mapper.getContributorById(task.getContributorId());
        var usr = mapper.getUserById(ctr.getUserId());

        if (!ctr.isOwner()) {

            if (taskState != TaskState.COMPLETED) {
                var moves = stateMoves.get(task.getTaskState());

                if (!moves.contains(taskState)) {
                    throw new DataCreationException(ErrorMessageUtil.TASK_MOVE_NOT_AVAILABLE);
                }

            }

            if (!stateMatches(taskState, usr.getRoles())) {
                throw new DataCreationException(ErrorMessageUtil.TASK_CONTRIBUTOR_UPDATE);
            }

        }

        task.setPrevState(task.getTaskState());
        task.setTaskState(taskState);

        if (taskState == TaskState.COMPLETED) {
            var endDate = new Date();
            task.setEndDate(endDate);
        }

        mapper.updateTask(task);
        logger.info("Updated task state to " + taskState.name() + " for id: " + taskId);

        return task;
    }


    /**
     * Delete(hard) task
     *
     * @param id - task id
     * @return true if task deleted , false if error
     */
    public boolean deleteTask(long id) {

        if (mapper.getTaskById(id) == null) {
            logger.info(String.format(LOG_FORMAT, ErrorMessageUtil.TASK_NOT_EXISTS, id));
            return false;
        }

        mapper.deleteTask(id);
        logger.info("Deleted task with id: " + id);
        return true;
    }

    /**
     * Find task by id
     *
     * @param taskId - task id
     * @throws DataExistenceException - task not exists
     * @return task POJO
     */
    public Task getTaskById(long taskId) throws DataExistenceException {


        var task = mapper.getTaskById(taskId);
        if (task == null) {
            logger.info(String.format(LOG_FORMAT, ErrorMessageUtil.TASK_NOT_EXISTS, taskId));
            throw new DataExistenceException(ErrorMessageUtil.TASK_NOT_EXISTS);
        }

        logger.info("Found task with id: " + taskId);

        return task;
    }


    /**
     * Get the list of tasks for project with offset and limit
     *
     * @param projectId - project id
     * @param offset    - start of list
     * @param limit     - list size
     * @throws DataExistenceException - project not exists
     * @return task POJO list
     */
    public List<Task> getTasksForProject(long projectId, int offset, int limit) throws DataExistenceException {

        var prj = mapper.getProjectById(projectId);
        if (prj == null) {
            logger.info(String.format(LOG_FORMAT, ErrorMessageUtil.PROJECT_EXISTS, projectId));
            throw new DataExistenceException(ErrorMessageUtil.PROJECT_NOT_EXISTS);
        }

        var tasks = mapper.getTasksForProject(projectId, offset, limit);
        logger.info("Found tasks for project with id: " + projectId);

        return tasks;
    }

    /**
     * Get the list of tasks of contributor with offset and limit
     *
     * @param ctrId  - contributor id
     * @param offset - start of list
     * @param limit  - list size
     * @throws DataExistenceException - contributor not exists
     * @return task POJO list
     */
    public List<Task> getTasksForContributor(long ctrId, int offset, int limit)
            throws DataExistenceException {

        var ctr = mapper.getContributorById(ctrId);
        if (ctr == null) {
            logger.info(String.format(LOG_FORMAT, ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS, ctrId));
            throw new DataExistenceException(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);
        }

        var tasks = mapper.getTasksForContributor(ctrId, offset, limit);
        logger.info("Found tasks for contributor with id: " + ctrId);

        return tasks;
    }

    /**
     * Get the list of tasks for user with offset and limit
     *
     * @param usrId  - user id
     * @param offset - start of list
     * @param limit  - list size
     * @throws DataExistenceException - user not exists
     * @return task POJO list
     */
    public List<Task> getTasksForUser(long usrId, int offset, int limit)
            throws DataExistenceException {

        var usr = mapper.getUserById(usrId);
        if (usr == null) {
            logger.info(String.format(LOG_FORMAT, ErrorMessageUtil.USER_NOT_EXISTS, usrId));
            throw new DataExistenceException(ErrorMessageUtil.USER_NOT_EXISTS);
        }

        var tasks = mapper.getTasksForUser(usrId, offset, limit);
        logger.info("Found tasks for user with id: " + usrId);

        return tasks;
    }

    /**
     * Get the list of tasks for sprint with offset and limit
     *
     * @param sprintId - sprint id
     * @param offset   - start of list
     * @param limit    - list size
     * @throws DataExistenceException - sprint not exists
     * @return Object wrapper with task POJO or with error message
     */
    public List<Task> getTasksForSprint(long sprintId, int offset, int limit) throws DataExistenceException {

        var sprint = mapper.getSprintById(sprintId);
        if (sprint == null) {
            logger.info(String.format(LOG_FORMAT, ErrorMessageUtil.SPRINT_NOT_EXISTS, sprintId));
            throw new DataExistenceException(ErrorMessageUtil.SPRINT_NOT_EXISTS);
        }

        var tasks = mapper.getTasksForSprint(sprintId, offset, limit);
        logger.info("Found tasks for sprint with id: " + sprintId);

        return tasks;
    }


    /**
     * Map each task state to the list of available states it can be moved to
     *
     * @return Map of states and available moves
     */
    private Map<TaskState, List<TaskState>> initMoves() {
        Map<TaskState, List<TaskState>> stateMoves = new HashMap<>();

        for (TaskState ts : EnumSet.allOf(TaskState.class)) {
            switch (ts) {
                case CREATED:
                    stateMoves.put(ts, List.of(TaskState.IN_TEST, TaskState.IN_DEPLOYMENT, TaskState.IN_DEVELOPMENT, TaskState.PAUSED));
                    break;
                case IN_DEVELOPMENT:
                    stateMoves.put(ts, List.of(TaskState.IN_CODE_REVIEW, TaskState.PAUSED));
                    break;
                case IN_CODE_REVIEW:
                    stateMoves.put(ts, List.of(TaskState.IN_DEVELOPMENT, TaskState.IN_TEST, TaskState.PAUSED));
                    break;
                case IN_TEST:
                    stateMoves.put(ts, List.of(TaskState.IN_DEVELOPMENT, TaskState.IN_DEPLOYMENT, TaskState.PAUSED, TaskState.COMPLETED));
                    break;
                case IN_DEPLOYMENT:
                    stateMoves.put(ts, List.of(TaskState.IN_DEVELOPMENT, TaskState.IN_TEST, TaskState.COMPLETED, TaskState.PAUSED));
                    break;
                default:
                    break;
            }
        }

        return stateMoves;
    }

    /**
     * Checkk if current role can work with current state
     *
     * @return true - can work, false - can't work
     */
    private boolean stateMatches(TaskState state, String roles) {
        var rolesList = List.of(roles.split(";"));


        switch (state) {
            case CREATED:
            case PAUSED:
            case COMPLETED:
                return true;
            case IN_DEVELOPMENT:
            case IN_CODE_REVIEW:
                return rolesList.contains(DEVELOPER.name());
            case IN_TEST:
                return rolesList.contains(QA.name());
            case IN_DEPLOYMENT:
                return rolesList.contains(DEV_OPS.name());
            default:
                return false;
        }
    }

}
