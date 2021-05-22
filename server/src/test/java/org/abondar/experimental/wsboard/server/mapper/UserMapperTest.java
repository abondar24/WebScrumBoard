package org.abondar.experimental.wsboard.server.mapper;



import org.junit.Ignore;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserMapperTest extends MapperTest {

    @Test
    public void insertUserTest() {
        cleanData();
        var user = createUser();
        assertTrue(user.getId() > 0);
    }


    @Test
    public void updateUserTest() {
        cleanData();
        var user = createUser();

        var newLogin = "login1";
        user.setLogin(newLogin);
        userMapper.updateUser(user);

        user = userMapper.getUserById(user.getId());

        assertEquals(newLogin, user.getLogin());
    }

    @Test
    public void updateAvatarTest() {
        var user = createUser();
        var img = "data/:base64,";

        user.setAvatar(img);
        userMapper.updateUser(user);

        user = userMapper.getUserById(user.getId());

        assertNotNull(user.getAvatar());

        cleanData();
    }

    @Test
    public void getUserByLoginTest() {
        cleanData();
        var user = createUser();
        var res = userMapper.getUserByLogin(user.getLogin());

        assertEquals(user.getId(), res.getId());
    }


    @Test
    public void getUserByIdTest() {
        cleanData();
        var user = createUser();
        var res = userMapper.getUserById(user.getId());

        assertEquals(user.getId(), res.getId());
    }

    @Test
    public void getUsersByIdsTest() {
        cleanData();
        var user = createUser();
        var user1 = createUser();
        var res = userMapper.getUsersByIds(List.of(user.getId(), user1.getId()));

        assertEquals(2, res.size());
    }

    @Test
    public void getProjectOwnerTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        createContributor(user.getId(), project.getId(), true);

        var res = userMapper.getProjectOwner(project.getId());
        assertEquals(user.getId(), res.getId());

    }

}
