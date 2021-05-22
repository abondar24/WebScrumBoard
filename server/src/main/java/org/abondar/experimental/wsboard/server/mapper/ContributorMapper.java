package org.abondar.experimental.wsboard.server.mapper;

import org.abondar.experimental.wsboard.server.datamodel.Contributor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Interface for contributor mapper
 * @author a.bondar
 */
@Mapper
@Qualifier("contributorMapper")
public interface ContributorMapper {

    void insertContributor(@Param("contributor") Contributor contributor);

    void updateContributor(@Param("contributor") Contributor contributor);

    Contributor getContributorById(@Param("id") long id);

    Contributor getContributorByLogin(@Param("projectId") long projectId, @Param("login")String login);

    Contributor getContributorByUserAndProject(@Param("userId") long userId, @Param("projectId") Long projectId);

    List<Contributor> getContributorsByUserId(@Param("userId") long userId, @Param("offset") int offset, @Param("limit") int limit);

    Integer countProjectContributors(@Param("projectId") long projectId);

    void deactivateUserContributors(@Param("userId") long userId);

    void deactivateProjectContributors(@Param("projectId") long projectId);

    void deleteProjectContributors(@Param("projectId") long projectId);

    @Delete("DELETE FROM contributor")
    void deleteContributors();
}
