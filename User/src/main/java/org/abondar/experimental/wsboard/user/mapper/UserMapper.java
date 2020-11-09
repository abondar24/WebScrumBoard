package org.abondar.experimental.wsboard.user.mapper;


import org.abondar.experimental.wsboard.user.data.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * User mapper for db calls
 */

@Mapper
public interface UserMapper {

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
