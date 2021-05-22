package org.abondar.experimental.wsboard.server.mapper;

import org.abondar.experimental.wsboard.server.datamodel.Sprint;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@Mapper
@Qualifier("sprintMapper")
public interface SprintMapper {

    void insertSprint(@Param("sprint") Sprint sprint);

    void updateSprint(@Param("sprint") Sprint sprint);

    Sprint getSprintByName(@Param("name") String name);

    Sprint getSprintById(@Param("id") long id);

    Sprint getCurrentSprint(@Param("prId") long prId);

    List<Sprint> getSprints(@Param("projectId") long projectId, @Param("offset") int offset, @Param("limit") Integer limit);

    Integer countSprints(@Param("prjId") long prjId);

    void deleteProjectSprints(@Param("projectId") long projectId);

    void deleteSprint(@Param("id") long id);

    @Delete(" DELETE FROM sprint")
    void deleteSprints();

}
