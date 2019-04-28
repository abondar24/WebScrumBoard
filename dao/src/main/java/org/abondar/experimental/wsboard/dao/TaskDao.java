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


public class TaskDao extends BaseDao {

    private static Logger logger = LoggerFactory.getLogger(TaskDao.class);


    private Map<TaskState, List<TaskState>> stateMoves;

    public TaskDao(DataMapper mapper) {
        super(mapper);

        this.stateMoves = initMoves();
    }

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


    public boolean deleteTask(long id) {

        if (mapper.getTaskById(id) == null) {
            logger.info(ErrorMessageUtil.TASK_NOT_EXISTS + "with id: " + id);
            return false;
        }

        mapper.deleteTask(id);
        logger.info("Deleted task with id: " + id);
        return true;
    }

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
