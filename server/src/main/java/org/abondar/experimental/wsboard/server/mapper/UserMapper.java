package org.abondar.experimental.wsboard.server.mapper;


import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Interface for mybatis mapper for user queries
 * @author a.bondar
 */
@Mapper
@Qualifier("userMapper")
public interface UserMapper  {
    void insertUser(@Param("user") User user);

    void updateUser(@Param("user") User user);

    User getUserByLogin(@Param("login") String login);

    User getUserById(@Param("id") long id);

    List<User> getUsersByIds(@Param("idList") List<Long> idList);

    User getProjectOwner(@Param("projectId") Long projectId);

    List<User> getContributorsForProject(@Param("projectId") long projectId, @Param("offset") int offset, @Param("limit") int limit);

    @Delete("DELETE FROM wsuser")
    void deleteUsers();

}
