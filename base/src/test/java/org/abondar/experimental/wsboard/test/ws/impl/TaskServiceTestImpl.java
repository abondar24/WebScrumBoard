package org.abondar.experimental.wsboard.test.ws.impl;

import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.Sprint;
import org.abondar.experimental.wsboard.datamodel.task.Task;
import org.abondar.experimental.wsboard.datamodel.task.TaskState;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.ws.service.TaskService;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response createTask(@FormParam("ctrId") long contributorId,
                               @FormParam("startDate") String startDate,
                               @FormParam("devOps") boolean devOpsEnabled) {
        try {
            Date stDate = convertDate(startDate);

            if (testContributor.getId() != contributorId) {
                return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS).build();
            }

            testTask = new Task(contributorId, stDate, devOpsEnabled);
            testTask.setTaskState(TaskState.CREATED);

            return Response.ok(testTask).build();

        } catch (DataCreationException ex) {
            return Response.status(Response.Status.PARTIAL_CONTENT).entity(ex.getLocalizedMessage()).build();
        }
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateTask(@FormParam("id") long taskId,
                               @FormParam("ctrId") Long contributorId,
                               @FormParam("devOps") boolean devOpsEnabled,
                               @FormParam("storyPoints") Integer storyPoints) {

        if (testTask.getId() != taskId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.TASK_NOT_EXISTS).build();
        }

        if (testContributor.getId() != contributorId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS).build();
        }

        testTask.setContributorId(contributorId);
        testTask.setDevOpsEnabled(devOpsEnabled);
        testTask.setStoryPoints(storyPoints);

        return Response.ok(testTask).build();
    }

    @POST
    @Path("/update_sprint")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateTaskSprint(@FormParam("id") long taskId,
                                     @FormParam("sprintId") long sprintId) {

        if (testTask.getId() != taskId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.TASK_NOT_EXISTS).build();
        }

        if (testSprint.getId() != sprintId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.SPRINT_NOT_EXISTS).build();
        }

        testTask.setSprintId(sprintId);

        return Response.ok(testTask).build();
    }

    @POST
    @Path("/update_state")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateTaskState(@FormParam("id") long taskId,
                                    @FormParam("state") String state) {

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

    @GET
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response deleteTask(@QueryParam("id") long id) {
        if (testTask.getId() != id) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.TASK_NOT_EXISTS).build();
        }

        return Response.ok().build();
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getTaskById(@QueryParam("id") long taskId) {
        if (testTask.getId() != taskId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.TASK_NOT_EXISTS).build();
        }

        return Response.ok(testTask).build();
    }

    @GET
    @Path("/find_project_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getTasksForProject(@QueryParam("prId") long projectId,
                                       @QueryParam("offset") int offset,
                                       @QueryParam("limit") int limit) {
        if (testProject.getId() != projectId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        var tasks = List.of(testTask, new Task(), new Task());


        if (offset == -1) {
            return Response.ok(tasks).build();
        }

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

    @GET
    @Path("/find_contributor_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getTasksForContributor(@QueryParam("ctrId") long ctrId,
                                           @QueryParam("offset") int offset,
                                           @QueryParam("limit") int limit) {
        if (testContributor.getId() != ctrId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        var tasks = List.of(testTask, new Task(), new Task());

        if (offset == -1) {
            return Response.ok(tasks).build();
        }


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

    @GET
    @Path("/find_user_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getTasksForUser(@QueryParam("usrId") long usrId,
                                    @QueryParam("offset") int offset,
                                    @QueryParam("limit") int limit) {

        if (testUser.getId() != usrId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        var tasks = List.of(testTask, new Task(), new Task());

        if (offset == -1) {
            return Response.ok(tasks).build();
        }


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

    @GET
    @Path("/find_sprint_tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getTasksForSprint(@QueryParam("spId") long sprintId,
                                      @QueryParam("offset") int offset,
                                      @QueryParam("limit") int limit) {
        if (testUser.getId() != sprintId) {
            return Response.status(Response.Status.NOT_FOUND).entity(LogMessageUtil.PROJECT_NOT_EXISTS).build();
        }

        var tasks = List.of(testTask, new Task(), new Task());

        if (offset == -1) {
            return Response.ok(tasks).build();
        }

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


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create_user")
    public Response createUser() {


        testUser = new User("test", "test", "test", "testy", "test", "test");
        testUser.setId(10);


        return Response.ok(testUser).build();
    }


    @POST
    @Path("/create_project")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProject() {


        testProject = new Project("test", new Date());
        testProject.setId(10);

        return Response.ok(testProject).build();
    }


    @GET
    @Path("/create_contributor")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createContributor() {

        testContributor = new Contributor(testUser.getId(), testProject.getId(), false);
        testContributor.setId(10);

        return Response.ok(testContributor).build();
    }


    @GET
    @Path("/create_sprint")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSprint() {

        testSprint = new Sprint();

        return Response.ok(testSprint).build();
    }

    private TaskState getTaskState(String state) throws DataExistenceException {
        try {
            return TaskState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DataExistenceException(LogMessageUtil.TASK_STATE_UNKNOWN);
        }
    }

}
