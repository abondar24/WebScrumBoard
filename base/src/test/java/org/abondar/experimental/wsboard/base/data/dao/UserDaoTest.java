package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.datamodel.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserDaoTest {

    private static Logger logger = LoggerFactory.getLogger(UserDaoTest.class);

    @Autowired
    private DataMapper mapper;

    @Autowired
    private UserDao dao;


    @Test
    public void createUserTest() throws Exception {
        logger.info("Create user test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        assertNotNull(dao);
        var usr = dao.createUser(login, password, email, firstName, lastName, roles);

        assertNull(usr.getMessage());
        assertTrue(usr.getObject().getId() > 0);

        mapper.deleteUsers();
    }


    @Test
    public void createUserLoginExistsTest() throws Exception {
        logger.info("Create user test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = dao.createUser(login, password, email, firstName, lastName, roles);
        var usr1 = dao.createUser(login, password, email, firstName, lastName, roles);


        assertEquals(ErrorMessageUtil.USER_EXISTS, usr1.getMessage());
        assertNull(usr1.getObject());

        mapper.deleteUsers();
    }

    @Test
    public void createUserLoginNoRolesTest() throws Exception {
        logger.info("Create user test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        List<String> roles = List.of();

        var usr = dao.createUser(login, password, email, firstName, lastName, roles);

        assertEquals(ErrorMessageUtil.NO_ROLES, usr.getMessage());
        assertNull(usr.getObject());

        mapper.deleteUsers();
    }

    @Test
    public void updateUserLoginTest() throws Exception {
        logger.info("Update user login test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = dao.createUser(login, password, email, firstName, lastName, roles);
        usr = dao.updateLogin("login1", usr.getObject().getId());
        assertNull(usr.getMessage());

        mapper.deleteUsers();
    }

    @Test
    public void updateUserLoginExistsTest() throws Exception {
        logger.info("Update user login exists test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = dao.createUser(login, password, email, firstName, lastName, roles);
        usr = dao.updateLogin(login, usr.getObject().getId());
        assertEquals(ErrorMessageUtil.USER_EXISTS, usr.getMessage());

        mapper.deleteUsers();
    }


    @Test
    public void updateUserLoginNotExistsTest() {
        logger.info("Update user login not exists test");

        var usr = dao.updateLogin("login", 1);
        assertEquals(ErrorMessageUtil.USER_NOT_EXIST, usr.getMessage());

    }

    @Test
    public void updatePasswordTest() throws Exception {
        logger.info("Update user password test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = dao.createUser(login, password, email, firstName, lastName, roles);
        usr = dao.updatePassword(password, "newPed", usr.getObject().getId());
        assertNull(usr.getMessage());

        mapper.deleteUsers();
    }

    @Test
    public void updatePasswordUserNotFoundTest() throws Exception {
        logger.info("Update user password user not found test");

        var usr = dao.updatePassword("pwd", "newPed", 100);
        assertEquals(ErrorMessageUtil.USER_NOT_EXIST, usr.getMessage());

    }

    @Test
    public void updatePasswordUnathorizedTest() throws Exception {
        logger.info("Update user password unauthorized test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = dao.createUser(login, password, email, firstName, lastName, roles);
        usr = dao.updatePassword("randomPwd", "newPed", usr.getObject().getId());
        assertEquals(ErrorMessageUtil.UNAUTHORIZED, usr.getMessage());

        mapper.deleteUsers();
    }

    @Test
    public void updateUserTest() throws Exception {
        logger.info("Update user password test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = dao.createUser(login, password, email, firstName, lastName, roles);

        usr = dao.updateUser(usr.getObject().getId(), "name1", "name2", "email1@email.com", List.of(UserRole.Manager.name()));
        assertNull(usr.getMessage());


        mapper.deleteUsers();
    }


    @Test
    public void updateUserNullFieldTest() throws Exception {
        logger.info("Update user password test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = dao.createUser(login, password, email, firstName, lastName, roles);

        usr = dao.updateUser(usr.getObject().getId(), null, null, null, List.of());
        assertNull(usr.getMessage());


        mapper.deleteUsers();
    }

    @Test
    public void updateUserEmptyFieldTest() throws Exception {
        logger.info("Update user password test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = dao.createUser(login, password, email, firstName, lastName, roles);

        usr = dao.updateUser(usr.getObject().getId(), null, "", null, List.of());
        assertNull(usr.getMessage());


        mapper.deleteUsers();
    }


    @Test
    public void updateUserAvatarTest() throws Exception {
        logger.info("Update user password test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = dao.createUser(login, password, email, firstName, lastName, roles);

        var avatar = new byte[512];
        usr = dao.updateUserAvatar(usr.getObject().getId(), avatar);
        assertNull(usr.getMessage());

        mapper.deleteUsers();
    }

    @Test
    public void updateUserAvatarNullTest() throws Exception {
        logger.info("Update user password test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = dao.createUser(login, password, email, firstName, lastName, roles);

        usr = dao.updateUserAvatar(usr.getObject().getId(), null);
        assertEquals(ErrorMessageUtil.USER_AVATAR_EMPTY, usr.getMessage());

        mapper.deleteUsers();
    }

    @Test
    public void updateUserAvatarEmptyTest() throws Exception {
        logger.info("Update user password test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = dao.createUser(login, password, email, firstName, lastName, roles);

        var avatar = new byte[]{};
        usr = dao.updateUserAvatar(usr.getObject().getId(), avatar);
        assertEquals(ErrorMessageUtil.USER_AVATAR_EMPTY, usr.getMessage());

        mapper.deleteUsers();
    }

    @Test
    public void deleteUserTest() throws Exception {
        logger.info("Update user password test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = dao.createUser(login, password, email, firstName, lastName, roles);

        usr = dao.updateUserAvatar(usr.getObject().getId(), new byte[512]);
        usr = dao.deleteUser(usr.getObject().getId());

        assertNull(usr.getMessage());
        assertEquals("deleted", usr.getObject().getLogin());

        mapper.deleteUsers();
    }

    @Test
    public void loginUserTest() throws Exception {
        logger.info("Update user password test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        dao.createUser(login, password, email, firstName, lastName, roles);
        var res = dao.loginUser(login, password);

        assertTrue(res.isBlank());

        mapper.deleteUsers();
    }

    @Test
    public void logoutUserTest() throws Exception {
        logger.info("Update user password test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = dao.createUser(login, password, email, firstName, lastName, roles);
        var res = dao.logoutUser(usr.getObject().getId());

        assertTrue(res.isBlank());

        mapper.deleteUsers();
    }


}