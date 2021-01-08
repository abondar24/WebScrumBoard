package org.abondar.experimental.wsboard.dao;


import org.abondar.experimental.wsboard.server.exception.DataCreationException;
import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.abondar.experimental.wsboard.server.exception.InvalidHashException;
import org.abondar.experimental.wsboard.server.util.PasswordUtil;
import org.abondar.experimental.wsboard.server.datamodel.user.UserRole;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UserDaoTest extends BaseDaoTest {

    @Test
    public void createUserTest() throws Exception {
        cleanData();
        var usr = createUser();

        assertTrue(usr.getId() > 0);

    }


    @Test
    public void createUserLoginExistsTest() throws Exception {
        cleanData();

        createUser();

        assertThrows(DataExistenceException.class, this::createUser);

    }

    @Test
    public void createUserBlankDataTest() {
        cleanData();
        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = "";


        assertThrows(DataCreationException.class, () ->
                userDao.createUser(login, password, email, firstName, lastName, roles));

    }

    @Test
    public void createUserNonEnglishTest() throws Exception {
        cleanData();
        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "Иван";
        var lastName = "Иванов";
        var roles = UserRole.DEVELOPER.name();

        var usr = userDao.createUser(login,email,password,firstName,lastName,roles);


        assertTrue(usr.getId() > 0);

    }

    @Test
    public void updateUserLoginTest() throws Exception {
        cleanData();

        var usr = createUser();
        var id = usr.getId();
        usr = userDao.updateLogin("login1", usr.getId());

        assertEquals(id, usr.getId());

    }

    @Test
    public void updateUserLoginExistsTest() throws Exception {
        cleanData();

        var usr = createUser();

        assertThrows(DataExistenceException.class, () -> userDao.updateLogin(usr.getLogin(), usr.getId()));

    }


    @Test
    public void updateUserLoginNotExistsTest() {
        cleanData();

        assertThrows(DataExistenceException.class, () -> userDao.updateLogin("login", 1));
    }

    @Test
    public void updatePasswordTest() throws Exception {
        cleanData();

        var usr = createUser();
        var id = usr.getId();
        usr = userDao.updatePassword("pwd", "newPwd", usr.getId());

        assertEquals(id, usr.getId());

    }

    @Test
    public void updatePasswordUserNotFoundTest() {
        cleanData();

        assertThrows(DataExistenceException.class, () ->
                userDao.updatePassword("pwd", "newPwd", 100));
    }

    @Test
    public void updatePasswordUnathorizedTest() throws Exception {
        cleanData();

        var usr = createUser();

        assertThrows(InvalidHashException.class, () ->
                userDao.updatePassword("randomPwd", "newPed", usr.getId()));

    }

    @Test
    public void updateUserTest() throws Exception {
        cleanData();

        var usr = createUser();
        var id = usr.getId();
        usr = userDao.updateUser(usr.getId(), "name1", "name2",
                "email1@email.com", UserRole.DEVELOPER.name() + ";", "data;base64");

        assertEquals(id, usr.getId());

    }


    @Test
    public void updateUserNullFieldTest() throws Exception {
        cleanData();

        var usr = createUser();
        var id = usr.getId();
        usr = userDao.updateUser(usr.getId(), null, null, null, null, null);

        assertEquals(id, usr.getId());

    }

    @Test
    public void updateUserEmptyFieldTest() throws Exception {
        cleanData();

        var usr = createUser();
        var id = usr.getId();

        usr = userDao.updateUser(usr.getId(), null, "", null, null, null);
        assertEquals(id, usr.getId());

    }

    @Test
    public void resetPasswordTest() throws Exception {
        cleanData();

        var usr = createUser();
        var id = usr.getId();

        userDao.resetPassword(id);

        usr = userDao.findUserById(id);
        assertTrue(PasswordUtil.verifyPassword("reset", usr.getPassword()));

    }

    @Test
    public void resetPasswordUserNotFoundTest() throws Exception {
        cleanData();

        assertThrows(DataExistenceException.class, () -> userDao.resetPassword(10));
    }

    @Test
    public void findUserByIdTest() throws Exception {
        cleanData();

        var usr = createUser();
        var id = usr.getId();

        usr = userDao.findUserById(id);
        assertEquals(id, usr.getId());

    }


    @Test
    public void findUserNotFoundByIdTest() {
        cleanData();

        assertThrows(DataExistenceException.class, () -> userDao.findUserById(10));
    }


    @Test
    public void findUserByLoginTest() throws Exception {
        cleanData();

        var usr = createUser();
        var login = usr.getLogin();

        usr = userDao.findUserByLogin(login);
        assertEquals(login, usr.getLogin());
    }


    @Test
    public void findUserNotFoundByLoginTest() {
        cleanData();

        assertThrows(DataExistenceException.class, () -> userDao.findUserByLogin("test"));
    }

    @Test
    public void findUsersByIdsTest() throws Exception {
        cleanData();

        var usr = createUser();
        var id = usr.getId();

        var res = userDao.findUsersByIds(List.of(id));
        assertEquals(1, res.size());

    }

    @Test
    public void findUsersByIdsNotExistingIdsTest() throws Exception {
        cleanData();

        var usr = createUser();
        var id = usr.getId();

        var res = userDao.findUsersByIds(List.of(id, 7L, 3L));
        assertEquals(1, res.size());

    }


    @Test
    public void deleteUserTest() throws Exception {
        cleanData();

        var usr = createUser();

        var project = createProject();
        contributorDao.createContributor(usr.getId(), project.getId(), true);

        var delUser = userDao.createUser("usr1", "pwd", "ss",
                "fname", "lname", UserRole.DEVELOPER.name() + ";");
        contributorDao.createContributor(delUser.getId(), project.getId(), false);

        delUser = userDao.deleteUser(delUser.getId());

        assertEquals("deleted", delUser.getLogin());

    }

    @Test
    public void deleteUserNoContributorsTest() throws Exception {
        cleanData();

        var delUser = userDao.createUser("usr1", "pwd", "ss",
                "fname", "lname", UserRole.DEVELOPER.name() + ";");

        delUser = userDao.deleteUser(delUser.getId());

        assertEquals("deleted", delUser.getLogin());

    }

    @Test
    public void deleteUserIsOwnerTest() throws Exception {
        cleanData();

        var usr = createUser();

        var project = createProject();
        contributorDao.createContributor(usr.getId(), project.getId(), true);

        assertThrows(DataCreationException.class, () -> userDao.deleteUser(usr.getId()));

    }



}
