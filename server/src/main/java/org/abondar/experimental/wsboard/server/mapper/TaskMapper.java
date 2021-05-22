package org.abondar.experimental.wsboard.server.mapper;

import org.abondar.experimental.wsboard.server.datamodel.task.Task;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Interface for mybatis mapper
 *
 * @author a.bondar
 */
@Mapper
@Qualifier("taskMapper")
public interface TaskMapper {

    void insertTask(@Param("task") Task task);

    void updateTask(@Param("task") Task task);

    void updateTaskSprint(@Param("id") Long id, @Param("sprintId") long sprintId);

    void updateTasksSprint(@Param("idList") List<Long> idList, @Param("sprintId") long sprintId);

    Task getTaskById(@Param("id") long id);

    List<Task> getTasksForProject(@Param("projectId") long projectId, @Param("offset") int offset, @Param("limit") Integer limit);

    List<Task> getTasksForContributor(@Param("contributorId") long contributorId, @Param("offset") int offset, @Param("limit") int limit);

    List<Task> getTasksForUser(@Param("userId") long userId, @Param("offset") int offset, @Param("limit") int limit);

    List<Task> getTasksForSprint(@Param("sprintId") long sprintId, @Param("offset") int offset, @Param("limit") Integer limit);

    Integer countUserTasks(@Param("userId") long userId);

    Integer countContributorTasks(@Param("ctrId") long ctrId);

    Integer countSprintTasks(@Param("spId") long spId);

    void deleteProjectTasks(@Param("projectId") long projectId);

    void deleteTask(@Param("id") long id);

    @Delete("DELETE FROM task")
    void deleteTasks();

}
