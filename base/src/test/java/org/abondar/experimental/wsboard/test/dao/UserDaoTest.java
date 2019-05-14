package org.abondar.experimental.wsboard.test.dao;

import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.dao.ContributorDao;
import org.abondar.experimental.wsboard.dao.ProjectDao;
import org.abondar.experimental.wsboard.dao.UserDao;
import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.dao.exception.InvalidPasswordException;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserDaoTest {

    private static Logger logger = LoggerFactory.getLogger(UserDaoTest.class);

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
        logger.info("Create user test");

        var usr = createUser();

        assertTrue(usr.getId() > 0);

        mapper.deleteUsers();
    }


    @Test
    public void createUserLoginExistsTest() throws Exception {
        logger.info("Create user test");

        createUser();

        assertThrows(DataExistenceException.class, this::createUser);

        mapper.deleteUsers();
    }

    @Test
    public void createUserBlankDataTest() {
        logger.info("Create user test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        List<String> roles = List.of();


        assertThrows(DataCreationException.class, () ->
                userDao.createUser(login, password, email, firstName, lastName, roles));

        mapper.deleteUsers();
    }

    @Test
    public void updateUserLoginTest() throws Exception {
        logger.info("Update user login test");

        var usr = createUser();
        var id = usr.getId();
        usr = userDao.updateLogin("login1", usr.getId());

        assertEquals(id, usr.getId());

        mapper.deleteUsers();
    }

    @Test
    public void updateUserLoginExistsTest() throws Exception {
        logger.info("Update user login exists test");

        var usr = createUser();

        assertThrows(DataExistenceException.class, () -> userDao.updateLogin(usr.getLogin(), usr.getId()));

        mapper.deleteUsers();
    }


    @Test
    public void updateUserLoginNotExistsTest() {
        logger.info("Update user login not exists test");


        assertThrows(DataExistenceException.class, () -> userDao.updateLogin("login", 1));

    }

    @Test
    public void updatePasswordTest() throws Exception {
        logger.info("Update user password test");

        var usr = createUser();
        var id = usr.getId();
        usr = userDao.updatePassword("pwd", "newPwd", usr.getId());

        assertEquals(id, usr.getId());
        mapper.deleteUsers();
    }

    @Test
    public void updatePasswordUserNotFoundTest() {
        logger.info("Update user password user not found test");


        assertThrows(DataExistenceException.class, () ->
                userDao.updatePassword("pwd", "newPwd", 100));

    }

    @Test
    public void updatePasswordUnathorizedTest() throws Exception {
        logger.info("Update user password unauthorized test");

        var usr = createUser();

        assertThrows(InvalidPasswordException.class, () ->
                userDao.updatePassword("randomPwd", "newPed", usr.getId()));
        mapper.deleteUsers();
    }

    @Test
    public void updateUserTest() throws Exception {
        logger.info("Update user test");

        var usr = createUser();
        var id = usr.getId();
        usr = userDao.updateUser(usr.getId(), "name1", "name2",
                "email1@email.com", List.of(UserRole.Developer.name()), new byte[1024]);

        assertEquals(id, usr.getId());

        mapper.deleteUsers();
    }


    @Test
    public void updateUserNullFieldTest() throws Exception {
        logger.info("Update user password test");

        var usr = createUser();
        var id = usr.getId();
        usr = userDao.updateUser(usr.getId(), null, null, null, List.of(), null);

        assertEquals(id, usr.getId());

        mapper.deleteUsers();
    }

    @Test
    public void updateUserEmptyFieldTest() throws Exception {
        logger.info("Update user password test");

        var usr = createUser();
        var id = usr.getId();

        usr = userDao.updateUser(usr.getId(), null, "", null, List.of(), null);
        assertEquals(id, usr.getId());

        mapper.deleteUsers();
    }


    @Test
    public void updateUserAvatarEmptyTest() throws Exception {
        logger.info("Update user password test");

        var usr = createUser();
        var avatar = new byte[]{};

        assertThrows(DataCreationException.class, () ->
                userDao.updateUser(usr.getId(), null, "", null, List.of(), avatar));

        mapper.deleteUsers();
    }

    @Test
    public void deleteUserTest() throws Exception {
        logger.info("Delete user test");

        var usr = createUser();

        var project = createProject();
        contributorDao.createContributor(usr.getId(), project.getId(), true);

        var delUser = userDao.createUser("usr1", "pwd", "ss",
                "fname", "lname", List.of(UserRole.Developer.name()));
        contributorDao.createContributor(delUser.getId(), project.getId(), false);

        delUser = userDao.deleteUser(delUser.getId());

        assertEquals("deleted", delUser.getLogin());

        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();
    }

    @Test
    public void deleteUserIsOwnerTest() throws Exception {
        logger.info("Delete user is owner test");

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
        logger.info("Delete user contributor test");

        var usr = createUser();

        var project = createProject();
        contributorDao.createContributor(usr.getId(), project.getId(), true);

        var delUsr = userDao.createUser("testLogin", "psw", "aaa",
                "aa", "aa", List.of(UserRole.Developer.name()));
        var ctr = contributorDao.createContributor(delUsr.getId(), project.getId(), false);

        userDao.deleteUser(delUsr.getId());
        var ctrObj = mapper.getContributorById(ctr.getId());

        assertFalse(ctrObj.isActive());

        mapper.deleteContributors();
        mapper.deleteProjects();
        mapper.deleteUsers();
    }


    @Test
    public void loginUserTest() throws Exception {
        logger.info("Login user test");

        var usr = createUser();
        userDao.loginUser(usr.getLogin(), "pwd");

        mapper.deleteUsers();
    }

    @Test
    public void logoutUserTest() throws Exception {
        logger.info("Update user password test");

        var usr = createUser();
        userDao.logoutUser(usr.getId());

        mapper.deleteUsers();
    }


    private User createUser() throws Exception {
        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        return userDao.createUser(login, password, email, firstName, lastName, roles);
    }

    private Project createProject() throws Exception {
        var project = projectDao.createProject("test", new Date());
        return projectDao.updateProject(project.getId(), null, null, true, null);
    }

}
