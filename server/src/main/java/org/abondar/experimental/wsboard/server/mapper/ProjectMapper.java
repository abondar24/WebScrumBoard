package org.abondar.experimental.wsboard.server.mapper;

import org.abondar.experimental.wsboard.server.datamodel.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Interface for mybatis mapper for project queries
 */
@Mapper
@Qualifier("projectMapper")
public interface ProjectMapper {

    void insertProject(@Param("project") Project project);

    void updateProject(@Param("project") Project project);

    Project getProjectById(@Param("id") long id);

    Project getProjectByName(@Param("name") String name);

    List<Project> getUserProjects(@Param("userId") long userId);

    void deleteProject(@Param("id") long id);
}
