package org.abondar.experimental.wsboard.test.ws;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = WebScrumBoardApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

    private static String endpoint = "local://wsboard_test";
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
        Server server = factory.create();
        server.start();
    }

    @Test
    public void createUserTest() {
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


        assertEquals(10, user.getId());
        assertEquals(login, user.getLogin());

    }

    @Test
    public void createUserLoginExistsTest() {
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
        assertEquals(206, resp.getStatus());

        var err = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.BLANK_DATA, err);
    }

    @Test
    public void createUserNoRolesTest() {
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
        assertEquals(206, resp.getStatus());

        var err = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NO_ROLES, err);

    }


    @Test
    public void updateUserTest() {
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
    public void findUserByLoginTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        var usr = createUser();

        client.path("/user/find").accept(MediaType.APPLICATION_JSON).query("login", usr.getLogin());

        var resp = client.get();
        assertEquals(200, resp.getStatus());

        usr = resp.readEntity(User.class);
        assertEquals(10, usr.getId());
        assertEquals(login, usr.getLogin());
    }

    @Test
    public void findUserByLoginNotFoundTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        createUser();

        client.path("/user/find").accept(MediaType.APPLICATION_JSON).query("login", "test");

        var resp = client.get();
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }

    @Test
    public void findUsersByIdsTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        var usr = createUser();

        client.path("/user/find_by_ids").accept(MediaType.APPLICATION_JSON)
                .query("id", usr.getId())
                .query("id", 1L)
                .query("id", 2L);

        var resp = client.get();
        assertEquals(200, resp.getStatus());

        Collection<? extends User> users = client.getCollection(User.class);
        assertEquals(1, users.size());
    }

    @Test
    public void findUsersByIdsEmptyResTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        createUser();

        client.path("/user/find_by_ids").accept(MediaType.APPLICATION_JSON)
                .query("id", 1L)
                .query("id", 2L);

        var resp = client.get();
        assertEquals(204, resp.getStatus());
}

    @Test
    public void resetPasswordTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        var usr = createUser();

        client.path("/user/reset_pwd").accept(MediaType.APPLICATION_JSON).query("id", usr.getId());

        var resp = client.get();
        assertEquals(200, resp.getStatus());


    }

    @Test
    public void resetPasswordUserNotFoundTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        createUser();

        client.path("/user/reset_pwd").accept(MediaType.APPLICATION_JSON).query("id", 7);

        var resp = client.get();
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }

    @Test
    public void deleteUserTest() {
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
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();
        createTestContributor(usr.getId());

        client.path("/user/delete").query("id", usr.getId()).accept(MediaType.APPLICATION_JSON);

        var resp = client.get();
        assertEquals(501, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_IS_PROJECT_OWNER, msg);

    }

    @Test
    public void loginUserTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var form = new Form();
        form.param("login", usr.getLogin());
        form.param("password", password);


        client.path("/user/login").accept(MediaType.APPLICATION_JSON);

        var resp = client.post(form);
        assertEquals(200, resp.getStatus());

        var token = resp.getHeaders().get("Authorization").get(0);
        assertEquals("JWT testToken", token);

    }

    @Test
    public void loginUserNotExistsTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        createUser();

        var form = new Form();
        form.param("login", "blabla");
        form.param("password", password);


        client.path("/user/login").accept(MediaType.APPLICATION_JSON);

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);

    }


    @Test
    public void loginUserUnauthorizedTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var form = new Form();
        form.param("login", usr.getLogin());
        form.param("password", "blabla");


        client.path("/user/login").accept(MediaType.APPLICATION_JSON);

        var resp = client.post(form);
        assertEquals(401, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_UNAUTHORIZED, msg);

    }


    @Test
    public void logoutUserTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        client.path("/user/logout").query("id", usr.getId()).accept(MediaType.APPLICATION_JSON);

        var resp = client.get();
        assertEquals(200, resp.getStatus());

        var token = resp.getCookies().get("X-JWT-AUTH").getValue();
        assertEquals("", token);

    }

    @Test
    public void logoutUserNotFoundTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        createUser();

        client.path("/user/logout").query("id", 1024).accept(MediaType.APPLICATION_JSON);

        var resp = client.get();
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);

    }

    @Test
    public void enterCodeTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        client.path("/user/enter_code")
                .query("userId", usr.getId())
                .query("code", 12345)
                .accept(MediaType.APPLICATION_JSON);

        var resp = client.get();
        assertEquals(200, resp.getStatus());


    }

    @Test
    public void enterCodeUserNotFoundTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        createUser();

        client.path("/user/enter_code").query("userId", 1024).accept(MediaType.APPLICATION_JSON);

        var resp = client.get();
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);

    }

    @Test
    public void enterCodeNotMatchesTest() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        client.path("/user/enter_code")
                .query("userId", usr.getId())
                .query("code", 123)
                .accept(MediaType.APPLICATION_JSON);

        var resp = client.get();
        assertEquals(400, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CODE_NOT_MATCHES, msg);
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
