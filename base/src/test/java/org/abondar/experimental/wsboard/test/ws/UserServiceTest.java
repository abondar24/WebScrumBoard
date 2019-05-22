package org.abondar.experimental.wsboard.test.ws;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.abondar.experimental.wsboard.test.ws.impl.AuthServiceTestImpl;
import org.abondar.experimental.wsboard.test.ws.impl.UserServiceTestImpl;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

    private static Server server;
    private static String endpoint = "local://wsboard_test";
    private Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
    private String login = "login";
    private String email = "email@email.com";
    private String password = "pwd";
    private String firstName = "fname";
    private String lastName = "lname";
    private String userRoles = UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name();


    @BeforeAll
    public static void beforeMethod() {
        var factory = new JAXRSServerFactoryBean();
        factory.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        factory.setProvider(new JacksonJsonProvider());
        factory.setAddress(endpoint);
        factory.setServiceBean(new UserServiceTestImpl(new AuthServiceTestImpl()));
        server = factory.create();
        server.start();
    }

    @Test
    public void createUserTest() {
        logger.info("create user test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));


        client.path("/user/create").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("login", login);
        form.param("email", email);
        form.param("firstName", firstName);
        form.param("lastName", lastName);
        form.param("password", password);
        form.param("roles", userRoles);

        var resp = client.post(form);
        assertEquals(200, resp.getStatus());

        var user = resp.readEntity(User.class);
        var token = resp.getCookies().get("X-JWT-AUTH").getValue();


        assertEquals(10, user.getId());
        assertEquals(login, user.getLogin());
        assertFalse(token.isEmpty());

    }

    @Test
    public void createUserLoginExistsTest() {
        logger.info("create user login exists test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));


        client.path("/user/create").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("login", "testLogin");
        form.param("email", email);
        form.param("firstName", firstName);
        form.param("lastName", lastName);
        form.param("password", password);
        form.param("roles", userRoles);

        var resp = client.post(form);
        assertEquals(302, resp.getStatus());

        var err = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_EXISTS, err);


    }

    @Test
    public void createUserBlankDataTest() {
        logger.info("create user blank data test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));


        client.path("/user/create").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("login", login);
        form.param("email", email);
        form.param("firstName", firstName);
        form.param("lastName", null);
        form.param("password", password);
        form.param("roles", userRoles);

        var resp = client.post(form);
        assertEquals(204, resp.getStatus());

        var err = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.BLANK_DATA, err);


    }

    @Test
    public void createUserNoRolesTest() {
        logger.info("create user no roles test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));


        client.path("/user/create").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("login", login);
        form.param("email", email);
        form.param("firstName", firstName);
        form.param("lastName", lastName);
        form.param("password", password);
        form.param("roles", "salosalo");

        var resp = client.post(form);
        assertEquals(204, resp.getStatus());

        var err = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NO_ROLES, err);


    }


    @Test
    public void updateUserTest() {
        logger.info("update user test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var form = new Form();
        form.param("id", String.valueOf(usr.getId()));
        form.param("email", "newEmail");

        client.path("/user/update")
                .accept(MediaType.APPLICATION_JSON);

        var resp = client.post(form);
        assertEquals(200, resp.getStatus());

        usr = resp.readEntity(User.class);
        assertEquals("newEmail", usr.getEmail());
    }

    @Test
    public void updateUserNotFoundTest() {
        logger.info("update user not found test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        createUser();

        var form = new Form();
        form.param("id", "1024");
        form.param("email", "newEmail");

        client.path("/user/update")
                .accept(MediaType.APPLICATION_JSON);

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }

    @Test
    public void updateUserNoRolesTest() {
        logger.info("update user no roles found test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var form = new Form();
        form.param("id", String.valueOf(usr.getId()));
        form.param("roles", "newEmail");

        client.path("/user/update")
                .accept(MediaType.APPLICATION_JSON);

        var resp = client.post(form);
        assertEquals(204, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NO_ROLES, msg);
    }


    @Test
    public void updateUserAvatarTest() {
        logger.info("update user avatar test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var avatar = "avatar".getBytes();

        client.path("/user/update_avatar").query("id", usr.getId())
                .type(MediaType.APPLICATION_OCTET_STREAM)
                .accept(MediaType.APPLICATION_JSON);

        var resp = client.post(avatar);
        assertEquals(200, resp.getStatus());

        usr = resp.readEntity(User.class);
        assertNotNull(usr.getAvatar());
    }

    @Test
    public void updateUserNotFoundAvatarTest() {
        logger.info("update user not found avatar test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        createUser();

        var avatar = "avatar".getBytes();

        client.path("/user/update_avatar").query("id", "1024")
                .type(MediaType.APPLICATION_OCTET_STREAM)
                .accept(MediaType.APPLICATION_JSON);

        var resp = client.post(avatar);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }

    @Test
    public void updateUserAvatarEmptyTest() {
        logger.info("update user avatar empty test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var avatar = "".getBytes();

        client.path("/user/update_avatar").query("id", usr.getId())
                .type(MediaType.APPLICATION_OCTET_STREAM)
                .accept(MediaType.APPLICATION_JSON);

        var resp = client.post(avatar);
        assertEquals(500, resp.getStatus());

    }

    @Test
    public void updateUserLoginTest() {
        logger.info("update user login test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var form = new Form();
        form.param("id", String.valueOf(usr.getId()));
        form.param("login", "newLogin");


        client.path("/user/update_login").accept(MediaType.APPLICATION_JSON);

        var resp = client.post(form);
        assertEquals(200, resp.getStatus());

        usr = resp.readEntity(User.class);
        assertEquals("newLogin", usr.getLogin());
    }


    @Test
    public void updateUserNotFoundLoginTest() {
        logger.info("update user not found login test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        createUser();

        var form = new Form();
        form.param("id", "1024");
        form.param("login", "newLogin");


        client.path("/user/update_login").accept(MediaType.APPLICATION_JSON);

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }

    @Test
    public void updateUserLoginExistsTest() {
        logger.info("update user login exists test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var form = new Form();
        form.param("id", String.valueOf(usr.getId()));
        form.param("login", usr.getLogin());


        client.path("/user/update_login").accept(MediaType.APPLICATION_JSON);

        var resp = client.post(form);
        assertEquals(302, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_EXISTS, msg);
    }

    @Test
    public void updateUserPasswordTest() {
        logger.info("update user password test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var oldHash = usr.getPassword();
        var form = new Form();
        form.param("id", String.valueOf(usr.getId()));
        form.param("oldPassword", password);
        form.param("newPassword", "newPwd");


        client.path("/user/update_password").accept(MediaType.APPLICATION_JSON);

        var resp = client.post(form);
        assertEquals(200, resp.getStatus());

        usr = resp.readEntity(User.class);
        assertNotEquals(oldHash, usr.getPassword());
    }

    @Test
    public void updateUserNotFoundPasswordTest() {
        logger.info("update user not found password test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        createUser();

        var form = new Form();
        form.param("id", "1024");
        form.param("oldPassword", password);
        form.param("newPassword", "newPwd");


        client.path("/user/update_password").accept(MediaType.APPLICATION_JSON);

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }


    @Test
    public void updateUserWrongPasswordTest() {
        logger.info("update user wrong password test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var form = new Form();
        form.param("id", String.valueOf(usr.getId()));
        form.param("oldPassword", "blabla");
        form.param("newPassword", "newPwd");

        client.path("/user/update_password").accept(MediaType.APPLICATION_JSON);

        var resp = client.post(form);
        assertEquals(401, resp.getStatus());

    }


    @Test
    public void deleteUserTest() {
        logger.info("delete user test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        client.path("/user/delete").query("id", usr.getId()).accept(MediaType.APPLICATION_JSON);

        var resp = client.get();
        assertEquals(200, resp.getStatus());

        usr = resp.readEntity(User.class);
        assertEquals("deleted", usr.getLogin());

    }


    @Test
    public void deleteUserNotFoundTest() {
        logger.info("delete user not found test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        createUser();

        client.path("/user/delete").query("id", 1024).accept(MediaType.APPLICATION_JSON);

        var resp = client.get();
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);

    }

    @Test
    public void deleteUseIsOwnerTest() {
        logger.info("delete user is owner test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();
        createTestContributor(usr.getId());

        client.path("/user/delete").query("id", usr.getId()).accept(MediaType.APPLICATION_JSON);

        var resp = client.get();
        assertEquals(501, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_IS_PROJECT_OWNER, msg);

    }


    private User createUser() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/user/create").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("login", login);
        form.param("email", email);
        form.param("firstName", firstName);
        form.param("lastName", lastName);
        form.param("password", password);
        form.param("roles", userRoles);


        var resp = client.post(form);

        return resp.readEntity(User.class);
    }

    private void createTestContributor(long usrId) {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/user/create_test_contributor").query("id", usrId);
        client.get();
        client.close();
    }

}
