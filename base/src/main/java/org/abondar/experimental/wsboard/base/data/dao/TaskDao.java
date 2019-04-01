package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.base.data.ObjectWrapper;
import org.abondar.experimental.wsboard.base.data.event.EventPublisher;
import org.abondar.experimental.wsboard.datamodel.task.Task;
import org.abondar.experimental.wsboard.datamodel.task.TaskState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.abondar.experimental.wsboard.datamodel.UserRole.*;


public class TaskDao extends BaseDao {

    private static Logger logger = LoggerFactory.getLogger(TaskDao.class);

/*
    private Map<TaskState, List<TaskState>> devTaskMoves;
    private Map<TaskState, List<TaskState>> testTaskMoves;
    private Map<TaskState, List<TaskState>> devOpsTaskMoves;
    private Map<TaskType, Map<TaskState, List<TaskState>>> typeMoves;
*/

    public TaskDao(DataMapper mapper, EventPublisher eventPublisher) {
        super(mapper, eventPublisher);
/*
        this.devTaskMoves = initDevMoves();
        this.testTaskMoves = initTestMoves();
        this.devOpsTaskMoves = initDevOpsMoves();

        this.typeMoves = new HashMap<>();
        typeMoves.put(TaskType.Development, devTaskMoves);
        typeMoves.put(TaskType.Testing, testTaskMoves);
        typeMoves.put(TaskType.DevOps, devOpsTaskMoves);
        */
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



        mapper.insertUpdateTask(task);
        logger.info("Created a task with id: " + task.getId());

        res.setObject(task);


        return res;
    }

    public ObjectWrapper<Task> updateTask(long taskId, long contributorId) {
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


        var ctr = mapper.getContributorById(task.getContributorId());
        var usr = mapper.getUserById(ctr.getUserId());

        if (!ctr.isOwner()) {
          /*
            var stateMoves = typeMoves.get(task.getType());

            if (stateMoves.isEmpty()) {
                logger.info(ErrorMessageUtil.TASK_NO_NEXT_STATES);
                res.setMessage(ErrorMessageUtil.TASK_NO_NEXT_STATES);

                return res;
            }

            if (!stateMoves.contains(taskState)) {

            }
            */

        }

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



}
