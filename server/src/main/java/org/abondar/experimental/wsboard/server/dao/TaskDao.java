package org.abondar.experimental.wsboard.server.dao;

import org.abondar.experimental.wsboard.server.datamodel.task.Task;
import org.abondar.experimental.wsboard.server.datamodel.task.TaskState;
import org.abondar.experimental.wsboard.server.datamodel.user.UserRole;
import org.abondar.experimental.wsboard.server.exception.DataCreationException;
import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.abondar.experimental.wsboard.server.util.LogMessageUtil;
import org.abondar.experimental.wsboard.server.mapper.DataMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Data access object for task
 *
 * @author a.bondar
 */
@Component
public class TaskDao extends BaseDao{

    private static final Logger logger = LoggerFactory.getLogger(TaskDao.class);


    private final Map<TaskState, List<TaskState>> stateMoves;

    @Autowired
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
     * @param taskName - task name
     * @param taskDescription - task description
     * @return task POJO
     * @throws DataExistenceException - contributor not exists or is not active
     */
    public Task createTask(long contributorId, Date startDate, boolean devOpsEnabled,
                           String taskName, String taskDescription)
            throws DataExistenceException {
        checkContributor(contributorId);

        var task = new Task(contributorId, startDate, devOpsEnabled, taskName, taskDescription);


        mapper.insertTask(task);
        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Created a task ", task.getId());
        logger.info(msg);

        return task;
    }


    /**
     * Create existing task
     *
     * @param taskId        - task id
     * @param contributorId - contributor id
     * @param devOpsEnabled - dev ops state to be ignored or not
     * @param storyPoints   - task story points
     * @param taskName - task name
     * @param taskDescription - task description
     * @return task POJO
     * @throws DataExistenceException - task or contributor does not exist or contributor is not active
     */
    public Task updateTask(long taskId, Long contributorId, Boolean devOpsEnabled,
                           Integer storyPoints, String taskName, String taskDescription)
            throws DataExistenceException {

        var task = getTaskById(taskId);

        if (contributorId != null) {
            checkContributor(contributorId);

            task.setContributorId(contributorId);

        }

        if (devOpsEnabled != null) {
            task.setDevOpsEnabled(devOpsEnabled);
        }

        if (storyPoints != null) {
            task.setStoryPoints(storyPoints);
        }

        if (taskName != null && !taskName.isBlank()) {
            task.setTaskName(taskName);
        }

        if (taskDescription != null && !taskDescription.isBlank()) {
            task.setTaskDescription(taskDescription);
        }



        mapper.updateTask(task);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Updated a task ", task.getId());
        logger.info(msg);

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
        var task = getTaskById(taskId);

        checkSprint(sprintId);

        mapper.updateTaskSprint(taskId, sprintId);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Updated task sprint ", task.getId());
        logger.info(msg);
        task.setSprintId(sprintId);

        return task;
    }

    /**
     * Set a sprint to a task
     *
     * @param taskIds   - list of task ids
     * @param sprintId - sprint id
     * @throws DataExistenceException - sprint doesn't exist
     */
    public void updateTasksSprint(List<Long> taskIds, long sprintId) throws DataExistenceException {

        checkSprint(sprintId);

        mapper.updateTasksSprint(taskIds, sprintId);
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

        var taskState = getTaskState(state);

        var task = getTaskById(taskId);

        if (task.getTaskState() == TaskState.COMPLETED) {
            logger.info(LogMessageUtil.TASK_ALREADY_COMPLETED);
            throw new DataCreationException(LogMessageUtil.TASK_ALREADY_COMPLETED);
        }

        if (taskState == TaskState.CREATED) {
            logger.info(LogMessageUtil.TASK_ALREADY_CREATED);
            throw new DataCreationException(LogMessageUtil.TASK_ALREADY_CREATED);
        }


        if ((task.getTaskState() == TaskState.PAUSED) && (task.getPrevState() != taskState)) {
            logger.info(LogMessageUtil.TASK_WRONG_STATE_AFTER_PAUSE);
            throw new DataCreationException(LogMessageUtil.TASK_WRONG_STATE_AFTER_PAUSE);
        }

        if (taskState == TaskState.IN_DEPLOYMENT && !task.isDevOpsEnabled()) {
            logger.info(LogMessageUtil.TASK_DEV_OPS_NOT_ENABLED);
            throw new DataCreationException(LogMessageUtil.TASK_DEV_OPS_NOT_ENABLED);
        }


        var ctr = mapper.getContributorById(task.getContributorId());
        var usr = mapper.getUserById(ctr.getUserId());

        if (!ctr.isOwner()) {


            var moves = stateMoves.get(task.getTaskState());

            if (!moves.contains(taskState)) {
                throw new DataCreationException(LogMessageUtil.TASK_MOVE_NOT_AVAILABLE);
            }


            if (!stateMatches(taskState, usr.getRoles())) {
                throw new DataCreationException(LogMessageUtil.TASK_CONTRIBUTOR_UPDATE);
            }

        }

        task.setPrevState(task.getTaskState());
        task.setTaskState(taskState);

        if (taskState == TaskState.COMPLETED) {
            var endDate = new Date();
            task.setEndDate(endDate);
        }

        mapper.updateTask(task);

        var msg = String.format("Updated task state to %s for id:  %d", taskState.name(), taskId);
        logger.info(msg);

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
            var msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.TASK_NOT_EXISTS, id);
            logger.info(msg);
            return false;
        }

        mapper.deleteTask(id);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Deleted task ", id);
        logger.info(msg);
        return true;
    }

    /**
     * Find task by id
     *
     * @param taskId - task id
     * @return task POJO
     * @throws DataExistenceException - task not exists
     */
    public Task getTaskById(long taskId) throws DataExistenceException {


        var task = mapper.getTaskById(taskId);
        if (task == null) {
            var msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.TASK_NOT_EXISTS, taskId);
            logger.info(msg);
            throw new DataExistenceException(LogMessageUtil.TASK_NOT_EXISTS);
        }

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Found task ", taskId);
        logger.info(msg);

        return task;
    }


    /**
     * Get the list of tasks for project with offset and limit
     *
     * @param projectId - project id
     * @param offset    - start of list
     * @param limit     - list size
     * @return task POJO list
     * @throws DataExistenceException - project not exists
     */
    public List<Task> getTasksForProject(long projectId, int offset, Integer limit) throws DataExistenceException {

        checkProject(projectId);

        var tasks = mapper.getTasksForProject(projectId, offset, limit);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Found tasks for project ", projectId);
        logger.info(msg);

        return tasks;
    }


    /**
     * Get the list of tasks for project which are not in any sprint
     *
     * @param projectId - project id
     * @return task POJO list
     * @throws DataExistenceException - project not exists
     */
    public List<Task> getNonSprintTasksForProject(long projectId) throws DataExistenceException{
        checkProject(projectId);

        var tasks = mapper.getTasksForProject(projectId, 0,null);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Found non-sprint tasks for project ", projectId);
        logger.info(msg);

        return  tasks.stream().filter(ts-> ts.getSprintId()==0).collect(Collectors.toList());
    }



    /**
     * Get the list of tasks of contributor with offset and limit
     *
     * @param ctrId  - contributor id
     * @param offset - start of list
     * @param limit  - list size
     * @return task POJO list
     * @throws DataExistenceException - contributor not exists
     */
    public List<Task> getTasksForContributor(long ctrId, int offset, int limit)
            throws DataExistenceException {

        checkContributor(ctrId);

        var tasks = mapper.getTasksForContributor(ctrId, offset, limit);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Found tasks for contributor ", ctrId);
        logger.info(msg);

        return tasks;
    }

    /**
     * Get the list of tasks for user with offset and limit
     *
     * @param usrId  - user id
     * @param offset - start of list
     * @param limit  - list size
     * @return task POJO list
     * @throws DataExistenceException - user not exists
     */
    public List<Task> getTasksForUser(long usrId, int offset, int limit)
            throws DataExistenceException {

        var usr = mapper.getUserById(usrId);
        if (usr == null) {
            var msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.USER_NOT_EXISTS, usrId);
            logger.info(msg);
            throw new DataExistenceException(LogMessageUtil.USER_NOT_EXISTS);
        }

        var tasks = mapper.getTasksForUser(usrId, offset, limit);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Found tasks for user ", usrId);
        logger.info(msg);

        return tasks;
    }


    /**
     * Counts the number of tasks for user
     * @param usrId - user to be counted
     * @return number of user tasks
     * @throws DataExistenceException - user not found
     */
    public Integer countUserTasks(Long usrId) throws DataExistenceException{

        var usr = mapper.getUserById(usrId);
        if (usr == null) {
            var msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.USER_NOT_EXISTS, usrId);
            logger.info(msg);
            throw new DataExistenceException(LogMessageUtil.USER_NOT_EXISTS);
        }

        var res = mapper.countUserTasks(usrId);
        var msg = String.format(LogMessageUtil.LOG_COUNT_FORMAT, "Counted tasks for user ", usrId,res);
        logger.info(msg);

        return res;

    }

    /**
     * Get the list of tasks for sprint with offset and limit
     *
     * @param sprintId - sprint id
     * @param offset   - start of list
     * @param limit    - list size
     * @return Object wrapper with task POJO or with error message
     * @throws DataExistenceException - sprint not exists
     */
    public List<Task> getTasksForSprint(long sprintId, int offset, Integer limit) throws DataExistenceException {

        checkSprint(sprintId);

        var tasks = mapper.getTasksForSprint(sprintId, offset, limit);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Found tasks for sprint ", sprintId);
        logger.info(msg);

        return tasks;
    }

    /**
     * Counts the number of tasks for sprint
     * @param spId - sprint to be counted
     * @return number of sprint tasks
     * @throws DataExistenceException - sprint not found
     */
    public Integer countSprintTasks(Long spId) throws DataExistenceException{

        checkSprint(spId);

        var res = mapper.countSprintTasks(spId);
        var msg = String.format(LogMessageUtil.LOG_COUNT_FORMAT, "Counted tasks for sprint ", spId,res);
        logger.info(msg);

        return res;

    }

    /**
     * Counts the number of tasks for contributor
     * @param ctrId - sprint to be counted
     * @return number of sprint tasks
     * @throws DataExistenceException - sprint not found
     */
    public Integer countContributorTasks(Long ctrId) throws DataExistenceException{

        checkContributor(ctrId);

        var res = mapper.countContributorTasks(ctrId);
        var msg = String.format(LogMessageUtil.LOG_COUNT_FORMAT, "Counted tasks for contributor ", ctrId,res);
        logger.info(msg);

        return res;

    }


    /**
     * Map each task state to the list of available states it can be moved to
     *
     * @return Map of states and available moves
     */
    private Map<TaskState, List<TaskState>> initMoves() {
        Map<TaskState, List<TaskState>> moves = new EnumMap<>(TaskState.class);

        for (TaskState ts : EnumSet.allOf(TaskState.class)) {
            switch (ts) {
                case CREATED:
                    moves.put(ts, List.of(TaskState.IN_TEST, TaskState.IN_DEPLOYMENT, TaskState.IN_DEVELOPMENT, TaskState.PAUSED));
                    break;
                case IN_DEVELOPMENT:
                    moves.put(ts, List.of(TaskState.IN_CODE_REVIEW, TaskState.PAUSED));
                    break;
                case IN_CODE_REVIEW:
                    moves.put(ts, List.of(TaskState.IN_DEVELOPMENT, TaskState.IN_TEST, TaskState.PAUSED));
                    break;
                case IN_TEST:
                    moves.put(ts, List.of(TaskState.IN_DEVELOPMENT, TaskState.IN_DEPLOYMENT, TaskState.PAUSED, TaskState.COMPLETED));
                    break;
                case IN_DEPLOYMENT:
                    moves.put(ts, List.of(TaskState.IN_DEVELOPMENT, TaskState.IN_TEST, TaskState.COMPLETED, TaskState.PAUSED));
                    break;
                case COMPLETED:
                default:
                    break;
            }
        }

        return moves;
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
                return rolesList.contains(UserRole.DEVELOPER.name());
            case IN_TEST:
                return rolesList.contains(UserRole.QA.name());
            case IN_DEPLOYMENT:
                return rolesList.contains(UserRole.DEV_OPS.name());
            default:
                return false;
        }
    }

    private TaskState getTaskState(String state) throws DataExistenceException {
        try {
            return TaskState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException ex) {
            logger.error(ex.getMessage());
            throw new DataExistenceException(LogMessageUtil.TASK_STATE_UNKNOWN);
        }
    }


    private void checkProject(long projectId) throws DataExistenceException {
        var prj = mapper.getProjectById(projectId);
        if (prj == null) {
            var msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.PROJECT_NOT_EXISTS, projectId);
            logger.info(msg);
            throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);
        }
    }

    /**
     * Check contributor for existence
     *
     * @param contributorId
     * @throws DataExistenceException
     */
    private void checkContributor(long contributorId) throws DataExistenceException {
        var ctr = mapper.getContributorById(contributorId);
        if (ctr == null || !ctr.isActive()) {
            var msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.CONTRIBUTOR_NOT_EXISTS, contributorId);
            logger.info(msg);
            throw new DataExistenceException(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS);

        }
    }

    /**
     * Check sprint for existence
     *
     * @param sprintId
     * @throws DataExistenceException
     */
    private void checkSprint(long sprintId) throws DataExistenceException {
        var sprint = mapper.getSprintById(sprintId);
        if (sprint == null) {
            var msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.SPRINT_NOT_EXISTS, sprintId);
            logger.info(msg);
            throw new DataExistenceException(LogMessageUtil.SPRINT_NOT_EXISTS);
        }
    }
}
