package org.abondar.experimental.wsboard.server.service.impl;

import org.abondar.experimental.wsboard.server.datamodel.Contributor;
import org.abondar.experimental.wsboard.server.datamodel.Project;
import org.abondar.experimental.wsboard.server.datamodel.Sprint;
import org.abondar.experimental.wsboard.server.datamodel.task.Task;
import org.abondar.experimental.wsboard.server.datamodel.task.TaskState;
import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.abondar.experimental.wsboard.server.exception.DataCreationException;
import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.abondar.experimental.wsboard.server.service.TaskService;
import org.abondar.experimental.wsboard.server.util.LogMessageUtil;


import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test implementation of task web service
 */
@Path("/task")
public class TaskServiceTestImpl implements TaskService {


    private Task testTask;
    private User testUser;
    private Project testProject;
    private Sprint testSprint;
    private Contributor testContributor;

    @Override
    public Response createTask(long contributorId, String startDate, boolean devOpsEnabled,
                               String taskName, String taskDescription) {
        try {
            Date stDate = convertDate(startDate);

            if (testContributor.getId() != contributorId) {
                return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS).build();
            }

            testTask = new Task(contributorId, stDate, devOpsEnabled, taskName, taskDescription);
            testTask.setTaskState(TaskState.CREATED);

            return Response.ok(testTask).build();

        } catch (DataCreationException ex) {
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
        }
    }

    @Override
    public Response updateTask(long taskId, Long contributorId, boolean devOpsEnabled, Integer storyPoints,
                               String taskName, String taskDescription) {

        if (testTask.getId() != taskId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.TASK_NOT_EXISTS).build();
        }

        if (testContributor.getId() != contributorId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS).build();
        }

        testTask.setContributorId(contributorId);
        testTask.setDevOpsEnabled(devOpsEnabled);
        testTask.setStoryPoints(storyPoints);
        testTask.setTaskName(taskName);
        testTask.setTaskDescription(taskDescription);

        return Response.ok(testTask).build();
    }

    @Override
    public Response updateTaskSprint(long taskId, long sprintId) {

        if (testTask.getId() != taskId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.TASK_NOT_EXISTS).build();
        }

        if (testSprint.getId() != sprintId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.SPRINT_NOT_EXISTS).build();
        }

        testTask.setSprintId(sprintId);

        return Response.ok(testTask).build();
    }

    @Override
    public Response updateTasksSprint(List<Long> ids, long sprintId) {
        if (ids.contains(testTask.getId())){
            return Response.ok().build();
        }

        return null;
    }

    @Override
    public Response updateTaskState(long taskId, String state) {

        if (testTask.getId() != taskId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.TASK_NOT_EXISTS).build();
        }

        try {
            var taskState = getTaskState(state);

            if (taskState == TaskState.CREATED) {
                return Response.status(Response.Status.CREATED).entity(LogMessageUtil.TASK_ALREADY_CREATED).build();
            }

            if (testTask.getTaskState() == TaskState.COMPLETED) {
                return Response.status(Response.Status.FOUND).entity(LogMessageUtil.TASK_ALREADY_COMPLETED).build();
            }

            if (taskState == TaskState.COMPLETED) {
                testTask.setEndDate(new Date());
            }

            if (!testTask.isDevOpsEnabled() && taskState == TaskState.IN_DEPLOYMENT) {
                return Response.status(Response.Status.NO_CONTENT)
                        .entity(LogMessageUtil.TASK_DEV_OPS_NOT_ENABLED).build();
            }

            if ((testTask.getTaskState() == TaskState.PAUSED) && (testTask.getPrevState() != taskState)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(LogMessageUtil.TASK_WRONG_STATE_AFTER_PAUSE).build();
            }

            if (taskState == TaskState.IN_CODE_REVIEW && testTask.getPrevState() == null) {
                return Response.status(Response.Status.NOT_IMPLEMENTED)
                        .entity(LogMessageUtil.TASK_MOVE_NOT_AVAILABLE).build();
            }

            if (taskState == TaskState.IN_TEST) {
                return Response.status(Response.Status.ACCEPTED).entity(LogMessageUtil.TASK_CONTRIBUTOR_UPDATE).build();
            }

            testTask.setPrevState(testTask.getTaskState());
            testTask.setTaskState(taskState);


            return Response.ok(testTask).build();
        } catch (DataExistenceException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getLocalizedMessage()).build();
        }

    }

    @Override
    public Response deleteTask(long id) {
        if (testTask.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.TASK_NOT_EXISTS).build();
        }

        return Response.ok().build();
    }

    @Override
    public Response getTaskById(long taskId) {
        if (testTask.getId() != taskId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.TASK_NOT_EXISTS).build();
        }

        return Response.ok(testTask).build();
    }

    @Override
    public Response getTasksForProject(long projectId, int offset, int limit,boolean all) {
        if (testProject.getId() != projectId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        var tasks = List.of(testTask, new Task(), new Task());



        tasks = tasks.stream()
                .skip(offset)
                .limit(limit)
                .filter(t -> t.getContributorId() == testContributor.getId()
                        && testContributor.getProjectId() == projectId)
                .collect(Collectors.toList());

        if (tasks.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.ok(tasks).build();
    }

    @Override
    public Response getTasksForContributor(long ctrId, int offset, int limit) {
        if (testContributor.getId() != ctrId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS).build();
        }

        var tasks = List.of(testTask, new Task(), new Task());

        tasks = tasks.stream()
                .skip(offset)
                .limit(limit)
                .filter(t -> t.getContributorId() == testContributor.getId())
                .collect(Collectors.toList());

        if (tasks.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.ok(tasks).build();
    }

    @Override
    public Response countContributorTasks(long ctrId) {
        return Response.ok(7).build();
    }

    @Override
    public Response getTasksForUser(long usrId, int offset, int limit) {

        if (testUser.getId() != usrId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.USER_NOT_EXISTS).build();
        }

        var tasks = List.of(testTask, new Task(), new Task());


        tasks = tasks.stream()
                .skip(offset)
                .limit(limit)
                .filter(t -> t.getContributorId() == testContributor.getId() && testContributor.getUserId() == usrId)
                .collect(Collectors.toList());

        if (tasks.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.ok(tasks).build();
    }

    @Override
    public Response countUserTasks(long ctrId) {
        return Response.ok(7).build();
    }

    @Override
    public Response getTasksForSprint(long sprintId, int offset, int limit) {
        if (testSprint.getId() != sprintId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.SPRINT_NOT_EXISTS).build();
        }

        testTask.setSprintId(testSprint.getId());
        var tasks = List.of(testTask, new Task(), new Task());

        tasks = tasks.stream()
                .skip(offset)
                .limit(limit)
                .filter(t -> t.getSprintId() == sprintId)
                .collect(Collectors.toList());

        if (tasks.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.ok(tasks).build();
    }

    @Override
    public Response countSprintTasks(long spId) {
        return Response.ok(7).build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/user")
    public Response createUser() {


        testUser = new User("test", "test", "test", "testy", "test", "test");
        testUser.setId(10);


        return Response.ok(testUser).build();
    }


    @POST
    @Path("/project")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProject() {


        testProject = new Project("test", new Date());
        testProject.setId(10);

        return Response.ok(testProject).build();
    }


    @POST
    @Path("/contributor")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createContributor() {

        testContributor = new Contributor(testUser.getId(), testProject.getId(), false);
        testContributor.setId(10);

        return Response.ok(testContributor).build();
    }


    @POST
    @Path("/sprint")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSprint() {

        testSprint = new Sprint();
        testSprint.setId(8);

        return Response.ok(testSprint).build();
    }

    private TaskState getTaskState(String state) throws DataExistenceException {
        try {
            return TaskState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DataExistenceException(LogMessageUtil.TASK_STATE_UNKNOWN);
        }
    }

    private Date convertDate(String strDate) throws DataCreationException {
        var format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            return format.parse(strDate);
        } catch (ParseException ex) {
            throw new DataCreationException(LogMessageUtil.PARSE_DATE_FAILED);
        }

    }

}
