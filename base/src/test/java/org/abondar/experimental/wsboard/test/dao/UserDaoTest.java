package org.abondar.experimental.wsboard.test.dao;

import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.dao.ContributorDao;
import org.abondar.experimental.wsboard.dao.ProjectDao;
import org.abondar.experimental.wsboard.dao.UserDao;
import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.dao.exception.InvalidHashException;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(classes = WebScrumBoardApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserDaoTest {

    @Autowired
    private DataMapper mapper;

    @Autowired
    @Qualifier("userDao")
    private UserDao userDao;

    @Autowired
    @Qualifier("projectDao")
    private ProjectDao projectDao;

    @Autowired
    @Qualifier("contributorDao")
    private ContributorDao contributorDao;

    @Test
    public void createUserTest() throws Exception {
        var usr = createUser();

        assertTrue(usr.getId() > 0);

        mapper.deleteUsers();
    }


    @Test
    public void createUserLoginExistsTest() throws Exception {
        createUser();

        assertThrows(DataExistenceException.class, this::createUser);

        mapper.deleteUsers();
    }

    @Test
    public void createUserBlankDataTest() {
        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        String roles = "";


        assertThrows(DataCreationException.class, () ->
                userDao.createUser(login, password, email, firstName, lastName, roles));

        mapper.deleteUsers();
    }

    @Test
    public void updateUserLoginTest() throws Exception {
        var usr = createUser();
        var id = usr.getId();
        usr = userDao.updateLogin("login1", usr.getId());

        assertEquals(id, usr.getId());

        mapper.deleteUsers();
    }

    @Test
    public void updateUserLoginExistsTest() throws Exception {
        var usr = createUser();

        assertThrows(DataExistenceException.class, () -> userDao.updateLogin(usr.getLogin(), usr.getId()));

        mapper.deleteUsers();
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
        mapper.deleteUsers();
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
        mapper.deleteUsers();
    }

    @Test
    public void updateUserTest() throws Exception {
        var usr = createUser();
        var id = usr.getId();
        usr = userDao.updateUser(usr.getId(), "name1", "name2",
                "email1@email.com", UserRole.DEVELOPER.name() + ";", new byte[1024]);

        assertEquals(id, usr.getId());

        mapper.deleteUsers();
    }


    @Test
    public void updateUserNullFieldTest() throws Exception {
        var usr = createUser();
        var id = usr.getId();
        usr = userDao.updateUser(usr.getId(), null, null, null, null, null);

        assertEquals(id, usr.getId());

        mapper.deleteUsers();
    }

    @Test
    public void updateUserEmptyFieldTest() throws Exception {
        var usr = createUser();
        var id = usr.getId();

        usr = userDao.updateUser(usr.getId(), null, "", null, null, null);
        assertEquals(id, usr.getId());

        mapper.deleteUsers();
    }

    @Test
    public void resetPasswordTest() throws Exception {
        var usr = createUser();
        var id = usr.getId();

        userDao.resetPassword(id);

        usr = userDao.findUserById(id);
        assertEquals("reset", usr.getPassword());

        mapper.deleteUsers();
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
        mapper.deleteUsers();
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
        mapper.deleteUsers();
    }


    @Test
    public void findUserNotFoundByLoginTest() {
        assertThrows(DataExistenceException.class, () -> userDao.findUserByLogin("test"));
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

        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();
    }

    @Test
    public void deleteUserIsOwnerTest() throws Exception {
        var usr = createUser();

        var project = createProject();
        contributorDao.createContributor(usr.getId(), project.getId(), true);

        assertThrows(DataCreationException.class, () -> userDao.deleteUser(usr.getId()));

        mapper.deleteContributors();
        mapper.deleteProjects();
        mapper.deleteUsers();
    }

    @Test
    public void deleteUserContributorTest() throws Exception {
        var usr = createUser();

        var project = createProject();
        contributorDao.createContributor(usr.getId(), project.getId(), true);

        var delUsr = userDao.createUser("testLogin", "psw", "aaa",
                "aa", "aa", UserRole.DEVELOPER.name() + ";");
        var ctr = contributorDao.createContributor(delUsr.getId(), project.getId(), false);

        userDao.deleteUser(delUsr.getId());
        var ctrObj = mapper.getContributorById(ctr.getId());

        assertFalse(ctrObj.isActive());

        mapper.deleteContributors();
        mapper.deleteProjects();
        mapper.deleteUsers();
    }


    @Test
    public void loginUserNotFoundTest() {
        assertThrows(DataExistenceException.class, () -> userDao.loginUser("login", "pwd"));

        mapper.deleteUsers();
    }

    @Test
    public void loginUserUnauthorizedTest() throws Exception {
        var user = createUser();
        assertThrows(InvalidHashException.class, () -> userDao.loginUser(user.getLogin(), "pass"));

        mapper.deleteUsers();
    }



    private User createUser() throws Exception {
        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = UserRole.DEVELOPER.name();

        return userDao.createUser(login, password, email, firstName, lastName, roles);
    }

    private Project createProject() throws Exception {
        var project = projectDao.createProject("test", new Date());
        return projectDao.updateProject(project.getId(), null, null, true, null);
    }

}
