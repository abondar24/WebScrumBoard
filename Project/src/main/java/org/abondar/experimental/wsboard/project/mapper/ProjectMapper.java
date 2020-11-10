package org.abondar.experimental.wsboard.project.mapper;

import org.abondar.experimental.wsboard.project.data.Project;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectMapper {

    void insertProject(@Param("project") Project project);

    void updateProject(@Param("project") Project project);

    Project getProjectById(@Param("id") long id);

    Project getProjectByName(@Param("name") String name);

    List<Project> getUserProjects(@Param("userId") long userId);

    void deleteProject(@Param("id") long id);

    void deactivateProjectContributors(@Param("projectId") long projectId);

    void deleteProjectTasks(@Param("projectId") long projectId);

    void deleteProjectSprints(@Param("projectId") long projectId);

    void deleteProjectContributors(@Param("projectId") long projectId);

    @Delete("DELETE FROM project")
    void deleteProjects();

}
