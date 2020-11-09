package org.abondar.experimental.wsboard.user.dao;


import org.abondar.experimental.wsboard.common.exception.DataCreationException;
import org.abondar.experimental.wsboard.common.exception.DataExistenceException;
import org.abondar.experimental.wsboard.common.exception.InvalidHashException;
import org.abondar.experimental.wsboard.dao.password.PasswordUtil;

import org.abondar.experimental.wsboard.user.data.User;
import org.abondar.experimental.wsboard.user.data.UserRole;
import org.apache.tools.ant.types.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserDaoTest  {

    @Autowired
    @Qualifier("userDao")
    protected  UserDao userDao;

    protected User createUser() throws Exception {
        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name();

        return userDao.createUser(login, password, email, firstName, lastName, roles);
    }

    protected User createUser(String login) throws Exception {
        if (login.isBlank()){
            return createUser();
        }
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name();

        return userDao.createUser(login, password, email, firstName, lastName, roles);
    }


    @Test
    public void createUserTest() throws Exception {
        var usr = createUser();

        assertTrue(usr.getId() > 0);

    }


    @Test
    public void createUserLoginExistsTest() throws Exception {
        createUser();
       assertThrows(DataExistenceException.class, this::createUser);

    }

    @Test
    public void createUserBlankDataTest() {
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
        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "Иван";
        var lastName = "Иванов";
        var roles = UserRole.DEVELOPER.name();

        var usr = userDao.createUser(login,email,password,firstName,lastName,roles);



    }

    @Test
    public void updateUserLoginTest() throws Exception {

        var usr = createUser();
        var id = usr.getId();
        usr = userDao.updateLogin("login1", usr.getId());

        assertEquals(id, usr.getId());

    }

    @Test
    public void updateUserLoginExistsTest() throws Exception {

        var usr = createUser();
        assertThrows(DataExistenceException.class, () -> userDao.updateLogin(usr.getLogin(), usr.getId()));

    }


    @Test
    public void updateUserLoginNotExistsTest() {
        assertThrows(DataExistenceException.class, () -> userDao.updateLogin("login", 1));
    }

    @Test
    public void updatePasswordTest() throws Exception {

        var usr = createUser();
        var id = usr.getId();
        usr = userDao.updatePassword("pwd", "newPwd", usr.getId());

        assertEquals(id, usr.getId());

    }

    @Test
    public void updatePasswordUserNotFoundTest() {

        assertThrows(DataExistenceException.class, () ->
                userDao.updatePassword("pwd", "newPwd", 100));
    }

    @Test
    public void updatePasswordUnathorizedTest() throws Exception {

        var usr = createUser();

        assertThrows(InvalidHashException.class, () ->
                userDao.updatePassword("randomPwd", "newPed", usr.getId()));

    }

    @Test
    public void updateUserTest() throws Exception {

        var usr = createUser();
        var id = usr.getId();
        usr = userDao.updateUser(usr.getId(), "name1", "name2",
                "email1@email.com", UserRole.DEVELOPER.name() + ";", "data;base64");

        assertEquals(id, usr.getId());

    }


    @Test
    public void updateUserNullFieldTest() throws Exception {

        var usr = createUser();
        var id = usr.getId();
        usr = userDao.updateUser(usr.getId(), null, null, null, null, null);

        assertEquals(id, usr.getId());

    }

    @Test
    public void updateUserEmptyFieldTest() throws Exception {

        var usr = createUser();
        var id = usr.getId();

        usr = userDao.updateUser(usr.getId(), null, "", null, null, null);
        assertEquals(id, usr.getId());

    }

    @Test
    public void resetPasswordTest() throws Exception {

        var usr = createUser();
        var id = usr.getId();

        userDao.resetPassword(id);

        usr = userDao.findUserById(id);
        assertTrue(PasswordUtil.verifyPassword("reset", usr.getPassword()));

    }

    @Test
    public void resetPasswordUserNotFoundTest() throws Exception {
        assertThrows(DataExistenceException.class, () -> userDao.resetPassword(10));
    }

    @Test
    public void findUserByIdTest() throws Exception {
        var usr = createUser();
        var id = usr.getId();

        usr = userDao.findUserById(id);
        assertEquals(id, usr.getId());

    }


    @Test
    public void findUserNotFoundByIdTest() {
        assertThrows(DataExistenceException.class, () -> userDao.findUserById(10));
    }


    @Test
    public void findUserByLoginTest() throws Exception {

        var usr = createUser();
        var login = usr.getLogin();

        usr = userDao.findUserByLogin(login);
        assertEquals(login, usr.getLogin());
    }


    @Test
    public void findUserNotFoundByLoginTest() {
        assertThrows(DataExistenceException.class, () -> userDao.findUserByLogin("test"));
    }

    @Test
    public void findUsersByIdsTest() throws Exception {

        var usr = createUser();
        var id = usr.getId();

        var res = userDao.findUsersByIds(List.of(id));
        assertEquals(1, res.size());

    }

    @Test
    public void findUsersByIdsNotExistingIdsTest() throws Exception {

        var usr = createUser();
        var id = usr.getId();

        var res = userDao.findUsersByIds(List.of(id, 7L, 3L));
        assertEquals(1, res.size());

    }


    @Test
    public void deleteUserTest() throws Exception {

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

        var delUser = userDao.createUser("usr1", "pwd", "ss",
                "fname", "lname", UserRole.DEVELOPER.name() + ";");

        delUser = userDao.deleteUser(delUser.getId());

        assertEquals("deleted", delUser.getLogin());

    }

    @Test
    public void deleteUserIsOwnerTest() throws Exception {
        var usr = createUser();

        var project = createProject();
        contributorDao.createContributor(usr.getId(), project.getId(), true);

        assertThrows(DataCreationException.class, () -> userDao.deleteUser(usr.getId()));

    }


}
