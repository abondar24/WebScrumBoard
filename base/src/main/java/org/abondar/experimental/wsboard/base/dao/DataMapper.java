package org.abondar.experimental.wsboard.base.dao;


import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.Task;
import org.abondar.experimental.wsboard.datamodel.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DataMapper {
    void insertUpdateUser(@Param("user") User user);

    void insertUpdateProject(@Param("project") Project project);

    void insertUpdateTask(@Param("task") Task task);

    void insertUpdateContributor(@Param("contributor") Contributor contributor);

    void updateUserAvatar(@Param("id") Long id, @Param("avatar") byte[] avatar);

    User getUserByLogin(@Param("login") String login);

    User getUserById(@Param("id") long id);

    Project getProjectById(@Param("id") long id);

    Project getProjectByName(@Param("name") String name);

    User getProjectOwner(@Param("projectId") Long projectId);

    List<User> getContributorsForProject(@Param("projectId") long projectId, @Param("offset") int offset, @Param("limit") int limit);

    Task getTaskById(@Param("id") long id);

    List<Task> getTasksForProject(@Param("projectId") long projectId, @Param("offset") int offset, @Param("limit") int limit);

    List<Task> getTasksForContributor(@Param("contributorId") long contributorId, @Param("offset") int offset, @Param("limit") int limit);

    List<Task> getTasksForUser(@Param("userId") long userId, @Param("offset") int offset, @Param("limit") int limit);

    void deleteTask(@Param("id") long id);

    void deleteProject(@Param("id") long id);

    void deleteContributor(@Param("id") long id);

    void deleteUsers();

    void deleteContributors();

    void deleteTasks();

    void deleteProjects();
}
