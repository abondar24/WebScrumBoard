package org.abondar.experimental.wsboard.base.dao;

import org.abondar.experimental.wsboard.base.Main;
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

import static org.junit.Assert.*;

@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class DaoTest {

    private static Logger logger = LoggerFactory.getLogger(MapperTest.class);

    @Autowired
    private DataMapper mapper;

    @Autowired
    private DAO dao;

    @Test
    public void createUserTest() throws Exception{
        logger.info("Create user test");

        String login="login";
        String email="email@email.com";
        String password="pwd";
        String firstName = "fname";
        String lastName = "lname";
        List<String> roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);

        assertNull(usr.getMessage());
        assertTrue(usr.getObject().getId()>0);

        mapper.deleteUsers();
    }


    @Test
    public void createUserLoginExistsTest() throws Exception{
        logger.info("Create user test");

        String login="login";
        String email="email@email.com";
        String password="pwd";
        String firstName = "fname";
        String lastName = "lname";
        List<String> roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);
        var usr1 = dao.createUser(login,password,email,firstName,lastName,roles);


        assertEquals(ErrorMessageUtil.USER_EXISTS,usr1.getMessage());
        assertNull(usr1.getObject());

        mapper.deleteUsers();
    }

    @Test
    public void createUserLoginNoRolesTest() throws Exception{
        logger.info("Create user test");

        String login="login";
        String email="email@email.com";
        String password="pwd";
        String firstName = "fname";
        String lastName = "lname";
        List<String> roles = List.of();

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);

        assertEquals(ErrorMessageUtil.NO_ROLES,usr.getMessage());
        assertNull(usr.getObject());

        mapper.deleteUsers();
    }

    @Test
    public void updateUserLoginTest() throws Exception{
        logger.info("Update user login test");

        String login="login";
        String email="email@email.com";
        String password="pwd";
        String firstName = "fname";
        String lastName = "lname";
        List<String> roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);
        usr = dao.updateLogin("login1",usr.getObject().getId());
        assertNull(usr.getMessage());

        mapper.deleteUsers();
    }

    @Test
    public void updateUserLoginExistsTest() throws Exception{
        logger.info("Update user login exists test");

        String login="login";
        String email="email@email.com";
        String password="pwd";
        String firstName = "fname";
        String lastName = "lname";
        List<String> roles = List.of(UserRole.Developer.name(),UserRole.DevOps.name());

        var usr = dao.createUser(login,password,email,firstName,lastName,roles);
        usr = dao.updateLogin(login,usr.getObject().getId());
        assertEquals(ErrorMessageUtil.USER_EXISTS,usr.getMessage());

        mapper.deleteUsers();
    }


    @Test
    public void updateUserLoginNotExistsTest() {
        logger.info("Update user login not exists test");

        var usr = dao.updateLogin("login",1);
        assertEquals(ErrorMessageUtil.USER_NOT_EXISTS,usr.getMessage());

    }


}
