package org.abondar.experimental.wsboard.base.data;


import org.abondar.experimental.wsboard.datamodel.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface DataMapper {
    void insertUpdateUser(@Param("user") User user);

    void insertUpdateProject(@Param("project") Project project);

    void insertUpdateTask(@Param("task") Task task);

    void insertUpdateContributor(@Param("contributor") Contributor contributor);

    void insertUpdateSprint(@Param("sprint")Sprint sprint);

    void updateUserAvatar(@Param("id") Long id, @Param("avatar") byte[] avatar);

    void updateTaskSprint(@Param("id")Long id,@Param("sprintId") long sprintId);

    void updateTaskStoryPoints(@Param("id")Long id,@Param("storyPoints") int storyPoints);

    void updateTaskEndDate(@Param("id")Long id, @Param("endDate") Date endDate);

    void updateTaskState(@Param("id")Long id, @Param("taskState") TaskState taskState,@Param("prevState") TaskState prevState);

    User getUserByLogin(@Param("login") String login);

    User getUserById(@Param("id") long id);

    Project getProjectById(@Param("id") long id);

    Project getProjectByName(@Param("name") String name);

    User getProjectOwner(@Param("projectId") Long projectId);

    List<User> getContributorsForProject(@Param("projectId") long projectId, @Param("offset") int offset, @Param("limit") int limit);

    Contributor getContributorById(@Param("id") long id);

    Contributor getContributorByUserId(@Param("userId") long userId);

    Task getTaskById(@Param("id") long id);

    List<Task> getTasksForProject(@Param("projectId") long projectId, @Param("offset") int offset, @Param("limit") int limit);

    List<Task> getTasksForContributor(@Param("contributorId") long contributorId, @Param("offset") int offset, @Param("limit") int limit);

    List<Task> getTasksForUser(@Param("userId") long userId, @Param("offset") int offset, @Param("limit") int limit);

    List<Task> getTasksForSprint(@Param("sprintId") long sprintId, @Param("offset") int offset, @Param("limit") int limit);

    Sprint getSprintById(@Param("id")long id);

    List<Sprint> getAllSprints(@Param("offset") int offset, @Param("limit") int limit);


    void deleteTask(@Param("id") long id);

    void deleteProject(@Param("id") long id);


    void deleteSprint(@Param("id") long id);

    void deleteUsers();

    void deleteContributors();

    void deleteTasks();

    void deleteProjects();

    void deleteSprints();
}
