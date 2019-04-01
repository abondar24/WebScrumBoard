package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.base.data.ObjectWrapper;
import org.abondar.experimental.wsboard.base.data.event.EventPublisher;
import org.abondar.experimental.wsboard.datamodel.task.Task;
import org.abondar.experimental.wsboard.datamodel.task.TaskState;
import org.abondar.experimental.wsboard.datamodel.task.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.abondar.experimental.wsboard.datamodel.UserRole.*;


public class TaskDao extends BaseDao {

    private static Logger logger = LoggerFactory.getLogger(TaskDao.class);


    private Map<TaskState, List<TaskState>> devTaskMoves;
    private Map<TaskState, List<TaskState>> testTaskMoves;
    private Map<TaskState, List<TaskState>> devOpsTaskMoves;
    private Map<TaskType, Map<TaskState, List<TaskState>>> typeMoves;


    public TaskDao(DataMapper mapper, EventPublisher eventPublisher) {
        super(mapper, eventPublisher);

        this.devTaskMoves = initDevMoves();
        this.testTaskMoves = initTestMoves();
        this.devOpsTaskMoves = initDevOpsMoves();

        this.typeMoves = new HashMap<>();
        typeMoves.put(TaskType.Development, devTaskMoves);
        typeMoves.put(TaskType.Testing, testTaskMoves);
        typeMoves.put(TaskType.DevOps, devOpsTaskMoves);
    }

    public ObjectWrapper<Task> createTask(long contributorId, String type, Date startDate, boolean devOpsEnabled) {
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


        TaskType taskType;
        try {
            taskType = TaskType.valueOf(type);
        } catch (IllegalArgumentException ex) {
            logger.error(ex.getMessage());
            res.setMessage(ErrorMessageUtil.TASK_TYPE_UNKNOWN);
            return res;
        }

        var usr = mapper.getUserById(ctr.getUserId());

        if (!typeMatches(taskType, usr.getRoles())) {
            logger.error(ErrorMessageUtil.TASK_TYPE_MISMATCH);
            res.setMessage(ErrorMessageUtil.TASK_TYPE_MISMATCH);
        }


        var task = new Task(contributorId, taskType, startDate, devOpsEnabled);

        if (taskType == TaskType.DevOps) {
            task.setDevOpsEnabled(true);
        }

        mapper.insertUpdateTask(task);
        logger.info("Created a task with id: " + task.getId());

        res.setObject(task);


        return res;
    }

    public ObjectWrapper<Task> updateTask(long taskId, long contributorId, String type) {
        ObjectWrapper<Task> res = new ObjectWrapper<>();

        var task = mapper.getTaskById(taskId);
        if (task == null) {
            logger.info(ErrorMessageUtil.TASK_NOT_EXISTS + "with id: " + taskId);
            res.setMessage(ErrorMessageUtil.TASK_NOT_EXISTS);
            return res;
        }

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

        if (type != null && !type.isBlank()) {
            TaskType taskType;
            try {
                taskType = TaskType.valueOf(type);
            } catch (IllegalArgumentException ex) {
                logger.error(ex.getMessage());
                res.setMessage(ErrorMessageUtil.TASK_TYPE_UNKNOWN);
                return res;
            }

            var usr = mapper.getUserById(ctr.getUserId());
            if (!typeMatches(taskType, usr.getRoles())) {
                logger.error(ErrorMessageUtil.TASK_TYPE_MISMATCH);
                res.setMessage(ErrorMessageUtil.TASK_TYPE_MISMATCH);
            }
            task.setType(taskType);
        }

        mapper.insertUpdateTask(task);
        logger.info("Updated a task with id: " + task.getId());

        res.setObject(task);
        return res;
    }


    public ObjectWrapper<Task> updateTaskStoryPoints(long taskId, Integer storyPoints) {
        ObjectWrapper<Task> res = new ObjectWrapper<>();

        var task = mapper.getTaskById(taskId);
        if (task == null) {
            logger.info(ErrorMessageUtil.TASK_NOT_EXISTS + "with id: " + taskId);
            res.setMessage(ErrorMessageUtil.TASK_NOT_EXISTS);
            return res;
        }

        if (storyPoints == null) {
            logger.info(ErrorMessageUtil.TASK_STORY_POINTS_NOT_SET);
            res.setMessage(ErrorMessageUtil.TASK_STORY_POINTS_NOT_SET);
            return res;
        }

        mapper.updateTaskStoryPoints(taskId, storyPoints);
        logger.info("Updated a task  story points for id: " + task.getId());
        task.setStoryPoints(storyPoints);
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

        if (taskState == TaskState.Paused) {
            task.setPrevState(task.getTaskState());
            task.setTaskState(TaskState.Paused);
        }

        if ((task.getTaskState() == TaskState.Paused) && (task.getPrevState() != taskState)) {
            logger.info(ErrorMessageUtil.TASK_WRONG_STATE_AFTER_PAUSE);
            res.setMessage(ErrorMessageUtil.TASK_WRONG_STATE_AFTER_PAUSE);

            return res;
        }

        if ((task.getType() == TaskType.Development) && taskState == TaskState.Completed) {
            logger.info(ErrorMessageUtil.TASK_COMPLETED_NOT_SUPPORTED);
            res.setMessage(ErrorMessageUtil.TASK_COMPLETED_NOT_SUPPORTED);

            return res;
        }

        var ctr = mapper.getContributorById(task.getContributorId());
        var usr = mapper.getUserById(ctr.getUserId());

        if (!ctr.isOwner()) {
            var stateMoves = typeMoves.get(task.getType());
            var nextStates = stateMoves.get(taskState);

            if (nextStates.isEmpty()) {
                logger.info(ErrorMessageUtil.TASK_NO_NEXT_STATES);
                res.setMessage(ErrorMessageUtil.TASK_NO_NEXT_STATES);

                return res;
            }

            if (!nextStates.contains(taskState)) {

            }


        }


        mapper.updateTaskState(taskId, taskState, task.getPrevState());
        logger.info("Updated task state to " + taskState.name() + " for id: " + taskId);
        return res;
    }


    //TODO: update task sprint


    public boolean deleteTask(long id) {

        if (mapper.getTaskById(id) == null) {
            logger.info(ErrorMessageUtil.TASK_NOT_EXISTS + "with id: " + id);
            return false;
        }

        mapper.deleteTask(id);
        logger.info("Deleted task with id: " + id);
        return true;
    }


    private Map<TaskState, List<TaskState>> initDevMoves() {
        Map<TaskState, List<TaskState>> stateMoves = new HashMap<>();

        for (TaskState ts : EnumSet.allOf(TaskState.class)) {
            switch (ts) {
                case Created:
                    stateMoves.put(ts, List.of(TaskState.InDevelopment));
                    break;
                case InDevelopment:
                    stateMoves.put(ts, List.of(TaskState.InCodeReview));
                    break;
                case InCodeReview:
                    stateMoves.put(ts, List.of(TaskState.InDevelopment, TaskState.InTest));
                    break;
            }
        }

        return stateMoves;
    }


    private Map<TaskState, List<TaskState>> initTestMoves() {
        Map<TaskState, List<TaskState>> stateMoves = new HashMap<>();

        for (TaskState ts : EnumSet.allOf(TaskState.class)) {
            switch (ts) {
                case Created:
                    stateMoves.put(ts, List.of(TaskState.InTest, TaskState.InDeployment));
                    break;
                case InTest:
                    stateMoves.put(ts, List.of(TaskState.InDeployment, TaskState.Completed));
                    break;
                case InDeployment:
                    stateMoves.put(ts, List.of(TaskState.InTest, TaskState.Completed));
                    break;
            }
        }

        return stateMoves;
    }

    private Map<TaskState, List<TaskState>> initDevOpsMoves() {
        Map<TaskState, List<TaskState>> stateMoves = new HashMap<>();

        for (TaskState ts : EnumSet.allOf(TaskState.class)) {
            switch (ts) {
                case Created:
                    stateMoves.put(ts, List.of(TaskState.InDeployment));
                    break;
                case InDeployment:
                    stateMoves.put(ts, List.of(TaskState.Completed));
                    break;
            }
        }

        return stateMoves;
    }


    private boolean typeMatches(TaskType type, String roles) {
        var rolesList = List.of(roles.split(";"));


        switch (type) {
            case Development:
                return rolesList.contains(Developer.name());
            case DevOps:
                return rolesList.contains(DevOps.name());
            case Testing:
                return rolesList.contains(QA.name());
            default:
                return false;
        }
    }

}
