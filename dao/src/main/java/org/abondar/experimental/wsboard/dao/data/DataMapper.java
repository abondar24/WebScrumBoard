package org.abondar.experimental.wsboard.dao.data;


import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.Sprint;
import org.abondar.experimental.wsboard.datamodel.task.Task;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Interface for mybatis mapper
 *
 * @author a.bondar
 */
@Mapper
public interface DataMapper {
    void insertUser(@Param("user") User user);

    void insertProject(@Param("project") Project project);

    void insertTask(@Param("task") Task task);

    void insertContributor(@Param("contributor") Contributor contributor);

    void insertSprint(@Param("sprint") Sprint sprint);

    void updateUser(@Param("user") User user);

    void updateProject(@Param("project") Project project);

    void updateContributor(@Param("contributor") Contributor contributor);

    void updateTask(@Param("task") Task task);

    void updateSprint(@Param("sprint") Sprint sprint);

    void updateTaskSprint(@Param("id")Long id,@Param("sprintId") long sprintId);

    User getUserByLogin(@Param("login") String login);

    User getUserById(@Param("id") long id);

    Project getProjectById(@Param("id") long id);

    Project getProjectByName(@Param("name") String name);

    User getProjectOwner(@Param("projectId") Long projectId);

    List<User> getContributorsForProject(@Param("projectId") long projectId, @Param("offset") int offset, @Param("limit") int limit);

    Contributor getContributorById(@Param("id") long id);

    Contributor getContributorByUserId(@Param("userId") long userId);

    User getUserByContributorId(@Param("ctrId") long ctrId);

    Task getTaskById(@Param("id") long id);

    List<Task> getTasksForProject(@Param("projectId") long projectId, @Param("offset") int offset, @Param("limit") int limit);

    List<Task> getTasksForContributor(@Param("contributorId") long contributorId, @Param("offset") int offset, @Param("limit") int limit);

    List<Task> getTasksForUser(@Param("userId") long userId, @Param("offset") int offset, @Param("limit") int limit);

    List<Task> getTasksForSprint(@Param("sprintId") long sprintId, @Param("offset") int offset, @Param("limit") int limit);

    Sprint getSprintByName(@Param("name") String name);

    Sprint getSprintById(@Param("id")long id);

    List<Sprint> getSprints(@Param("offset") int offset, @Param("limit") int limit);


    void deleteTask(@Param("id") long id);

    void deleteProject(@Param("id") long id);


    void deleteSprint(@Param("id") long id);

    void deleteUsers();

    void deleteContributors();

    void deleteTasks();

    void deleteProjects();

    void deleteSprints();
}