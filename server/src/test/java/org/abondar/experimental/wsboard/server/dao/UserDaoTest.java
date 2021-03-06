package org.abondar.experimental.wsboard.server.dao;


import org.abondar.experimental.wsboard.server.datamodel.Contributor;
import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.abondar.experimental.wsboard.server.datamodel.user.UserRole;
import org.abondar.experimental.wsboard.server.exception.DataCreationException;
import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.abondar.experimental.wsboard.server.exception.InvalidHashException;
import org.abondar.experimental.wsboard.server.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


public class UserDaoTest extends DaoTest {

    @InjectMocks
    private UserDao userDao;

    @Test
    public void createUserTest() throws Exception {
        when(userMapper.getUserByLogin(usr.getLogin())).thenReturn(null);
        doNothing().when(userMapper).insertUser(any(User.class));

        var res = userDao.createUser(usr.getLogin(), usr.getPassword(), usr.getEmail(), usr.getFirstName(), usr.getLastName(), usr.getRoles());
        assertEquals(res.getLogin(), usr.getLogin());

    }


    @Test
    public void createUserLoginExistsTest() {
        assertThrows(DataExistenceException.class, () -> {
            when(userMapper.getUserByLogin(usr.getLogin())).thenReturn(new User());
            userDao.createUser(usr.getLogin(), usr.getPassword(), usr.getEmail(), usr.getFirstName(), usr.getLastName(), usr.getRoles());
        });

    }

    @Test
    public void createUserBlankDataTest() {
        when(userMapper.getUserByLogin(usr.getLogin())).thenReturn(null);

        assertThrows(DataCreationException.class, () ->
                userDao.createUser(usr.getLogin(), usr.getPassword(), usr.getEmail(), usr.getFirstName(), usr.getLastName(), ""));

    }

    @Test
    public void createUserNonEnglishTest() throws Exception {
        var firstName = "Иван";
        var lastName = "Иванов";

        when(userMapper.getUserByLogin(usr.getLogin())).thenReturn(null);
        var res = userDao.createUser(usr.getLogin(), usr.getPassword(), usr.getEmail(), firstName, lastName, usr.getRoles());


        assertEquals(res.getFirstName(), firstName);

    }

    @Test
    public void updateUserLoginTest() throws Exception {
        var newLogin = "login1";

        when(userMapper.getUserByLogin(anyString())).thenReturn(null);
        when(userMapper.getUserById(usr.getId())).thenReturn(usr);
        doNothing().when(userMapper).updateUser(any(User.class));

        var res = userDao.updateLogin(newLogin, usr.getId());

        assertEquals(newLogin, res.getLogin());

    }

    @Test
    public void updateUserLoginExistsTest() {
        when(userMapper.getUserByLogin(usr.getLogin())).thenReturn(usr);
        assertThrows(DataExistenceException.class, () -> userDao.updateLogin(usr.getLogin(), usr.getId()));

    }


    @Test
    public void updateUserUserNotExistsTest() {
        when(userMapper.getUserById(1)).thenReturn(null);
        assertThrows(DataExistenceException.class, () -> userDao.updateLogin("login", 1));
    }

    @Test
    public void updatePasswordTest() throws Exception {
        var oldPwd = usr.getPassword();
        var pwd = PasswordUtil.createHash(usr.getPassword());
        usr.setPassword(pwd);
        var id = usr.getId();

        when(userMapper.getUserById(id)).thenReturn(usr);
        doNothing().when(userMapper).updateUser(any(User.class));

        var res = userDao.updatePassword(oldPwd, "newPwd", usr.getId());

        assertEquals(id, res.getId());

    }

    @Test
    public void updatePasswordUserNotFoundTest() {
        when(userMapper.getUserById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class, () ->
                userDao.updatePassword("pwd", "newPwd", 100));
    }

    @Test
    public void updatePasswordUnathorizedTest() {
        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        assertThrows(InvalidHashException.class, () ->
                userDao.updatePassword("randomPwd", "newPed", usr.getId()));

    }

    @Test
    public void updateUserTest() throws Exception {
        var id = usr.getId();

        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        doNothing().when(userMapper).updateUser(any(User.class));

        var res = userDao.updateUser(usr.getId(), "name1", "name2",
                "email1@email.com", UserRole.DEVELOPER.name() + ";", "data;base64");

        assertEquals(id, res.getId());

    }


    @Test
    public void updateUserNullFieldTest() throws Exception {
        var id = usr.getId();

        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        doNothing().when(userMapper).updateUser(any(User.class));

        var res = userDao.updateUser(usr.getId(), null, null, null, null, null);

        assertEquals(id, res.getId());

    }

    @Test
    public void updateUserEmptyFieldTest() throws Exception {
        var id = usr.getId();

        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        doNothing().when(userMapper).updateUser(any(User.class));

        var res = userDao.updateUser(usr.getId(), null, "", null, null, null);
        assertEquals(id, res.getId());

    }

    @Test
    public void resetPasswordTest() throws Exception {
        var id = usr.getId();

        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        doNothing().when(userMapper).updateUser(any(User.class));
        userDao.resetPassword(id);

        assertTrue(PasswordUtil.verifyPassword("reset", usr.getPassword()));

    }

    @Test
    public void resetPasswordUserNotFoundTest() {
        when(userMapper.getUserById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class, () -> userDao.resetPassword(10));
    }

    @Test
    public void findUserByIdTest() throws Exception {
        var id = usr.getId();

        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        var res = userDao.findUserById(id);

        assertEquals(id, res.getId());

    }


    @Test
    public void findUserNotFoundByIdTest() {
        when(userMapper.getUserById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class, () -> userDao.findUserById(10));
    }


    @Test
    public void findUserByLoginTest() throws Exception {
        var login = usr.getLogin();

        when(userMapper.getUserByLogin(anyString())).thenReturn(usr);

        var res = userDao.findUserByLogin(login);
        assertEquals(login, res.getLogin());
    }


    @Test
    public void findUserNotFoundByLoginTest() {
        when(userMapper.getUserByLogin(anyString())).thenReturn(null);

        assertThrows(DataExistenceException.class, () -> userDao.findUserByLogin("test"));
    }

    @Test
    public void findUsersByIdsTest() {
        var id = usr.getId();
        when(userMapper.getUsersByIds(List.of(usr.getId()))).thenReturn(List.of(usr));

        var res = userDao.findUsersByIds(List.of(id));
        assertEquals(1, res.size());

    }

    @Test
    public void deleteUserTest() throws Exception {
        userDao = new UserDao(userMapper, contributorMapper,new MockTransactionManager());

        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        when(contributorMapper.getContributorsByUserId(usr.getId(), -1, 0)).thenReturn(List.of(new Contributor()));
        doNothing().when(contributorMapper).deactivateUserContributors(anyLong());


        var delUser = userDao.deleteUser(usr.getId());

        assertEquals("deleted", delUser.getLogin());

    }

    @Test
    public void deleteUserNoContributorsTest() throws Exception {
        userDao = new UserDao(userMapper, contributorMapper,new MockTransactionManager());
        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        when(contributorMapper.getContributorsByUserId(usr.getId(), -1, 0)).thenReturn(List.of());

        var delUser = userDao.deleteUser(usr.getId());

        assertEquals("deleted", delUser.getLogin());

    }

    @Test
    public void deleteUserIsOwnerTest() {
        userDao = new UserDao(userMapper,contributorMapper, new MockTransactionManager());
        when(userMapper.getUserById(anyLong())).thenReturn(usr);

        var ctr = new Contributor();
        ctr.setUserId(usr.getId());
        ctr.setProjectId(prj.getId());
        ctr.setOwner(true);
        when(contributorMapper.getContributorsByUserId(usr.getId(), -1, 0)).thenReturn(List.of(ctr));

        assertThrows(DataCreationException.class, () -> userDao.deleteUser(usr.getId()));

    }


}
