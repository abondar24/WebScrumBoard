package org.abondar.experimental.wsboard.server.mapper;


import org.abondar.experimental.wsboard.server.datamodel.Contributor;
import org.abondar.experimental.wsboard.server.datamodel.task.Task;
import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.abondar.experimental.wsboard.server.datamodel.Project;
import org.abondar.experimental.wsboard.server.datamodel.SecurityCode;
import org.abondar.experimental.wsboard.server.datamodel.Sprint;
import org.apache.ibatis.annotations.Delete;
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

    void insertCode(@Param("securityCode") SecurityCode code);

    void updateUser(@Param("user") User user);

    void updateProject(@Param("project") Project project);

    void updateContributor(@Param("contributor") Contributor contributor);

    void updateTask(@Param("task") Task task);

    void updateSprint(@Param("sprint") Sprint sprint);

    void updateTaskSprint(@Param("id")Long id,@Param("sprintId") long sprintId);

    void updateTasksSprint(@Param("idList") List<Long> idList,@Param("sprintId") long sprintId);

    User getUserByLogin(@Param("login") String login);

    User getUserById(@Param("id") long id);

    List<User> getUsersByIds(@Param("idList") List<Long> idList);

    Project getProjectById(@Param("id") long id);

    Project getProjectByName(@Param("name") String name);

    List<Project> getUserProjects(@Param("userId") long userId);

    User getProjectOwner(@Param("projectId") Long projectId);

    List<User> getContributorsForProject(@Param("projectId") long projectId, @Param("offset") int offset, @Param("limit") int limit);

    Contributor getContributorById(@Param("id") long id);

    Contributor getContributorByLogin(@Param("projectId") long projectId, @Param("login")String login);

    Contributor getContributorByUserAndProject(@Param("userId") long userId, @Param("projectId") Long projectId);

    List<Contributor> getContributorsByUserId(@Param("userId") long userId, @Param("offset") int offset, @Param("limit") int limit);

    Integer countProjectContributors(@Param("projectId") long projectId);

    Task getTaskById(@Param("id") long id);

    SecurityCode getCodeByUserId(@Param("id") long id);

    Integer checkCodeExists(@Param("code") long code);

    List<Task> getTasksForProject(@Param("projectId") long projectId, @Param("offset") int offset, @Param("limit") Integer limit);

    List<Task> getTasksForContributor(@Param("contributorId") long contributorId, @Param("offset") int offset, @Param("limit") int limit);

    List<Task> getTasksForUser(@Param("userId") long userId, @Param("offset") int offset, @Param("limit") int limit);

    List<Task> getTasksForSprint(@Param("sprintId") long sprintId, @Param("offset") int offset, @Param("limit") Integer limit);

    Integer countUserTasks(@Param("userId") long userId);

    Integer countContributorTasks(@Param("ctrId") long ctrId);

    Integer countSprintTasks(@Param("spId") long spId);

    Sprint getSprintByName(@Param("name") String name);

    Sprint getSprintById(@Param("id")long id);

    Sprint getCurrentSprint(@Param("prId") long prId);

    List<Sprint> getSprints(@Param("projectId")long projectId, @Param("offset") int offset, @Param("limit") Integer limit);

    Integer countSprints(@Param("prjId") long prjId);

    void deactivateUserContributors(@Param("userId") long userId);

    void deactivateProjectContributors(@Param("projectId") long projectId);

    void deleteProjectTasks(@Param("projectId") long projectId);

    void deleteProjectSprints(@Param("projectId") long projectId);

    void deleteProjectContributors(@Param("projectId") long projectId);

    void deleteTask(@Param("id") long id);

    void deleteProject(@Param("id") long id);

    void deleteSprint(@Param("id") long id);

    void deleteCode(@Param("id") long id);

    @Delete("DELETE FROM wsuser")
    void deleteUsers();

    @Delete("DELETE FROM contributor")
    void deleteContributors();

    @Delete("DELETE FROM task")
    void deleteTasks();

    @Delete(" DELETE FROM project")
    void deleteProjects();

    @Delete(" DELETE FROM sprint")
    void deleteSprints();

    @Delete(" DELETE FROM security_code")
    void deleteCodes();


}