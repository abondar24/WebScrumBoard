package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.dao.data.ObjectWrapper;
import org.abondar.experimental.wsboard.datamodel.task.Task;
import org.abondar.experimental.wsboard.datamodel.task.TaskState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.abondar.experimental.wsboard.datamodel.UserRole.DevOps;
import static org.abondar.experimental.wsboard.datamodel.UserRole.Developer;
import static org.abondar.experimental.wsboard.datamodel.UserRole.QA;

/**
 * Data access object for task
 *
 * @author a.bondar
 */
public class TaskDao extends BaseDao {

    private static Logger logger = LoggerFactory.getLogger(TaskDao.class);


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
     * @return Object wrapper with task POJO or with error message
     */
    public ObjectWrapper<Task> createTask(long contributorId, Date startDate, boolean devOpsEnabled) {
        ObjectWrapper<Task> res = new ObjectWrapper<>();
        var ctr = mapper.getContributorById(contributorId);
        if (ctr == null) {
            logger.error(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);
            res.setMessage(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);
            return res;
        }

        if (!ctr.isActive()) {
            logger.error(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);
            res.setMessage(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);
            return res;
        }

        if (startDate == null) {
            logger.error(ErrorMessageUtil.TASK_START_DATE_NOT_SET);
            res.setMessage(ErrorMessageUtil.TASK_START_DATE_NOT_SET);
            return res;
        }


        var task = new Task(contributorId, startDate, devOpsEnabled);


        mapper.insertTask(task);
        logger.info("Created a task with id: " + task.getId());

        res.setObject(task);


        return res;
    }


    /**
     * Create existing task
     *
     * @param taskId        - task id
     * @param contributorId - contributor id
     * @param devOpsEnabled - dev ops state to be ignored or not
     * @param storyPoints   - task story points
     * @return Object wrapper with task POJO or with error message
     */
    public ObjectWrapper<Task> updateTask(long taskId, Long contributorId, Boolean devOpsEnabled, Integer storyPoints) {
        ObjectWrapper<Task> res = new ObjectWrapper<>();

        var task = mapper.getTaskById(taskId);
        if (task == null) {
            logger.info(ErrorMessageUtil.TASK_NOT_EXISTS + "with id: " + taskId);
            res.setMessage(ErrorMessageUtil.TASK_NOT_EXISTS);
            return res;
        }

        if (contributorId != null) {
            var ctr = mapper.getContributorById(contributorId);
            if (ctr == null) {
                logger.error(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);
                res.setMessage(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);
                return res;
            }

            if (!ctr.isActive()) {
                logger.error(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);
                res.setMessage(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);
                return res;
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

        res.setObject(task);
        return res;
    }

    /**
     * Set a sprint to a task
     *
     * @param taskId   - task id
     * @param sprintId - sprint id
     * @return Object wrapper with task POJO or with error message
     */
    public ObjectWrapper<Task> updateTaskSprint(long taskId, long sprintId) {
        ObjectWrapper<Task> res = new ObjectWrapper<>();

        var task = mapper.getTaskById(taskId);
        if (task == null) {
            logger.info(ErrorMessageUtil.TASK_NOT_EXISTS + "with id: " + taskId);
            res.setMessage(ErrorMessageUtil.TASK_NOT_EXISTS);
            return res;
        }

        var sprint = mapper.getSprintById(sprintId);
        if (sprint == null) {
            logger.info(ErrorMessageUtil.SPRINT_NOT_EXISTS + "with id: " + sprintId);
            res.setMessage(ErrorMessageUtil.SPRINT_NOT_EXISTS);
            return res;
        }

        mapper.updateTaskSprint(taskId, sprintId);
        logger.info("Updated task sprint for id: " + task.getId());
        task.setSprintId(sprintId);
        res.setObject(task);

        return res;
    }


    /**
     * Update state of task
     *
     * @param taskId - task id
     * @param state  - new state
     * @return Object wrapper with task POJO or with error message
     */
    public ObjectWrapper<Task> updateTaskState(long taskId, String state) {
        ObjectWrapper<Task> res = new ObjectWrapper<>();

        TaskState taskState;
        try {
            taskState = TaskState.valueOf(state);
        } catch (IllegalArgumentException ex) {
            logger.error(ex.getMessage());
            res.setMessage(ErrorMessageUtil.TASK_STATE_UNKNOWN);
            return res;
        }

        var task = mapper.getTaskById(taskId);
        if (task == null) {
            logger.info(ErrorMessageUtil.TASK_NOT_EXISTS + "with id: " + taskId);
            res.setMessage(ErrorMessageUtil.TASK_NOT_EXISTS);
            return res;
        }

        if (task.getTaskState() == TaskState.Completed) {
            logger.info(ErrorMessageUtil.TASK_ALREADY_COMPLETED);
            res.setMessage(ErrorMessageUtil.TASK_ALREADY_COMPLETED);

            return res;
        }

        if (taskState == TaskState.Created) {
            logger.info(ErrorMessageUtil.TASK_ALREADY_CREATED);
            res.setMessage(ErrorMessageUtil.TASK_ALREADY_CREATED);

            return res;
        }


        if ((task.getTaskState() == TaskState.Paused) && (task.getPrevState() != taskState)) {
            logger.info(ErrorMessageUtil.TASK_WRONG_STATE_AFTER_PAUSE);
            res.setMessage(ErrorMessageUtil.TASK_WRONG_STATE_AFTER_PAUSE);

            return res;
        }

        if (taskState == TaskState.InDeployment && !task.isDevOpsEnabled()) {
            logger.info(ErrorMessageUtil.TASK_DEV_OPS_NOT_ENABLED);
            res.setMessage(ErrorMessageUtil.TASK_DEV_OPS_NOT_ENABLED);

            return res;
        }


        var ctr = mapper.getContributorById(task.getContributorId());
        var usr = mapper.getUserById(ctr.getUserId());

        if (!ctr.isOwner()) {

            if (taskState != TaskState.Completed) {
                var moves = stateMoves.get(task.getTaskState());

                if (!moves.contains(taskState)) {
                    res.setMessage(ErrorMessageUtil.TASK_MOVE_NOT_AVAILABLE);
                    return res;
                }

            }

            if (!stateMatches(taskState, usr.getRoles())) {
                res.setMessage(ErrorMessageUtil.TASK_CONTRIBUTOR_UPDATE);
            }

        }

        task.setPrevState(task.getTaskState());
        task.setTaskState(taskState);

        if (taskState == TaskState.Completed) {
            var endDate = new Date();
            task.setEndDate(endDate);
        }

        mapper.updateTask(task);
        logger.info("Updated task state to " + taskState.name() + " for id: " + taskId);
        res.setObject(task);
        return res;
    }


    /**
     * Delete(hard) task
     *
     * @param id - task id
     * @return true if task deleted , false if error
     */
    public boolean deleteTask(long id) {

        if (mapper.getTaskById(id) == null) {
            logger.info(ErrorMessageUtil.TASK_NOT_EXISTS + "with id: " + id);
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
     * @return Object wrapper with task POJO or with error message
     */
    public ObjectWrapper<Task> getTaskById(long taskId) {
        ObjectWrapper<Task> res = new ObjectWrapper<>();

        var task = mapper.getTaskById(taskId);
        if (task == null) {
            logger.info(ErrorMessageUtil.TASK_NOT_EXISTS + "with id: " + taskId);
            res.setMessage(ErrorMessageUtil.TASK_NOT_EXISTS);
            return res;
        }

        logger.info("Found task with id: " + taskId);
        res.setObject(task);
        return res;
    }


    /**
     * Get the list of tasks for project with offset and limit
     *
     * @param projectId - project id
     * @param offset    - start of list
     * @param limit     - list size
     * @return Object wrapper with task POJO list or with error message
     */
    public ObjectWrapper<List<Task>> getTasksForProject(long projectId, int offset, int limit) {
        ObjectWrapper<List<Task>> res = new ObjectWrapper<>();

        var prj = mapper.getProjectById(projectId);
        if (prj == null) {
            logger.info(ErrorMessageUtil.PROJECT_NOT_EXISTS + "with id: " + projectId);
            res.setMessage(ErrorMessageUtil.PROJECT_NOT_EXISTS);
            return res;
        }

        var tasks = mapper.getTasksForProject(projectId, offset, limit);
        logger.info("Found tasks for project with id: " + projectId);
        res.setObject(tasks);

        return res;
    }

    /**
     * Get the list of tasks of contributor with offset and limit
     *
     * @param ctrId  - contributor id
     * @param offset - start of list
     * @param limit  - list size
     * @return Object wrapper with task POJO list or with error message
     */
    public ObjectWrapper<List<Task>> getTasksForContributor(long ctrId, int offset, int limit) {
        ObjectWrapper<List<Task>> res = new ObjectWrapper<>();

        var ctr = mapper.getContributorById(ctrId);
        if (ctr == null) {
            logger.info(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS + "with id: " + ctrId);
            res.setMessage(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);
            return res;
        }

        var tasks = mapper.getTasksForContributor(ctrId, offset, limit);
        logger.info("Found tasks for contributor with id: " + ctrId);
        res.setObject(tasks);

        return res;
    }

    /**
     * Get the list of tasks for user with offset and limit
     *
     * @param usrId  - user id
     * @param offset - start of list
     * @param limit  - list size
     * @return Object wrapper with task POJO list or with error message
     */
    public ObjectWrapper<List<Task>> getTasksForUser(long usrId, int offset, int limit) {
        ObjectWrapper<List<Task>> res = new ObjectWrapper<>();

        var usr = mapper.getUserById(usrId);
        if (usr == null) {
            logger.info(ErrorMessageUtil.USER_NOT_EXISTS + "with id: " + usrId);
            res.setMessage(ErrorMessageUtil.USER_NOT_EXISTS);
            return res;
        }

        var tasks = mapper.getTasksForUser(usrId, offset, limit);
        logger.info("Found tasks for user with id: " + usrId);
        res.setObject(tasks);

        return res;
    }

    /**
     * Get the list of tasks for sprint with offset and limit
     *
     * @param sprintId - sprint id
     * @param offset   - start of list
     * @param limit    - list size
     * @return Object wrapper with task POJO or with error message
     */
    public ObjectWrapper<List<Task>> getTasksForSprint(long sprintId, int offset, int limit) {
        ObjectWrapper<List<Task>> res = new ObjectWrapper<>();

        var sprint = mapper.getSprintById(sprintId);
        if (sprint == null) {
            logger.info(ErrorMessageUtil.SPRINT_NOT_EXISTS + " with id: " + sprintId);
            res.setMessage(ErrorMessageUtil.SPRINT_NOT_EXISTS);
            return res;
        }

        var tasks = mapper.getTasksForSprint(sprintId, offset, limit);
        logger.info("Found tasks for sprint with id: " + sprintId);
        res.setObject(tasks);

        return res;
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
                case Created:
                    stateMoves.put(ts, List.of(TaskState.InTest, TaskState.InDeployment, TaskState.InDevelopment, TaskState.Paused));
                    break;
                case InDevelopment:
                    stateMoves.put(ts, List.of(TaskState.InCodeReview, TaskState.Paused));
                    break;
                case InCodeReview:
                    stateMoves.put(ts, List.of(TaskState.InDevelopment, TaskState.InTest, TaskState.Paused));
                    break;
                case InTest:
                    stateMoves.put(ts, List.of(TaskState.InDevelopment, TaskState.InDeployment, TaskState.Paused, TaskState.Completed));
                    break;
                case InDeployment:
                    stateMoves.put(ts, List.of(TaskState.InDevelopment, TaskState.InTest, TaskState.Completed, TaskState.Paused));
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
            case InDevelopment:
            case InCodeReview:
                return rolesList.contains(Developer.name());
            case InTest:
                return rolesList.contains(QA.name());
            case InDeployment:
                return rolesList.contains(DevOps.name());
            default:
                return false;
        }
    }

}
