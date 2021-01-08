package org.abondar.experimental.wsboard.server.service;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.datamodel.user.user.User;
import org.abondar.experimental.wsboard.datamodel.user.user.UserRole;

import org.abondar.experimental.wsboard.ws.service.impl.AuthServiceTestImpl;
import org.abondar.experimental.wsboard.ws.service.impl.UserServiceTestImpl;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

    private static final String ENDPOINT = "local://wsboard_test";
    private final String login = "login";
    private final String email = "email@email.com";
    private final String password = "pwd";
    private final String firstName = "fname";
    private final String lastName = "lname";
    private final String userRoles = UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name();


    @BeforeAll
    public static void beforeMethod() {
        var factory = new JAXRSServerFactoryBean();
        factory.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        factory.setProvider(new JacksonJsonProvider());
        factory.setAddress(ENDPOINT);
        factory.setServiceBean(new UserServiceTestImpl(new AuthServiceTestImpl()));
        Server server = factory.create();
        server.start();
    }

    @Test
    public void createUserTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));


        client.path("/user").accept(MediaType.APPLICATION_JSON);

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
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/user").accept(MediaType.APPLICATION_JSON);

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
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));


        client.path("/user").accept(MediaType.APPLICATION_JSON);

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
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/user").accept(MediaType.APPLICATION_JSON);

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
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var form = new Form();
        form.param("email", "newEmail");


        client.path("/user/{id}",usr.getId())
                .accept(MediaType.APPLICATION_JSON);

        var resp = client.put(form);
        assertEquals(200, resp.getStatus());

        usr = resp.readEntity(User.class);
        assertEquals("newEmail", usr.getEmail());
    }

    @Test
    public void updateUserNotFoundTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var form = new Form();
        form.param("email", "newEmail");

        client.path("/user/7")
                .accept(MediaType.APPLICATION_JSON);

        var resp = client.put(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }

    @Test
    public void updateUserNoRolesTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var form = new Form();
        form.param("roles", "newEmail");

        client.path("/user/{id}",usr.getId())
                .accept(MediaType.APPLICATION_JSON);

        var resp = client.put(form);
        assertEquals(204, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NO_ROLES, msg);
    }


    @Test
    public void updateUserAvatarTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var avatar = "avatar".getBytes();

        client.path("/user/{id}/avatar",usr.getId())
                .type(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);


        var attrs = new ArrayList<Attachment>();
        attrs.add(new Attachment("file",avatar));
        var body = new MultipartBody(attrs);
        var resp = client.put(body);
        assertEquals(200, resp.getStatus());

        usr = resp.readEntity(User.class);
        assertNotNull(usr.getAvatar());
    }

    @Test
    public void updateUserNotFoundAvatarTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        createUser();

        var avatar = "avatar".getBytes();

        client.path("/user/1024/avatar")
                .type(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        var attrs = new ArrayList<Attachment>();
        attrs.add(new Attachment("file",avatar));
        var body = new MultipartBody(attrs);
        var resp = client.put(body);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }

    @Test
    public void updateUserAvatarEmptyTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var avatar = "".getBytes();

        client.path("/user/{id}/avatar",usr.getId())
                .type(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        var resp = client.put(avatar);
        assertEquals(500, resp.getStatus());

    }

    @Test
    public void updateUserLoginTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var form = new Form();
        form.param("login", "newLogin");


        client.path("/user/{id}/login",usr.getId()).accept(MediaType.APPLICATION_JSON);

        var resp = client.put(form);
        assertEquals(200, resp.getStatus());

        usr = resp.readEntity(User.class);
        assertEquals("newLogin", usr.getLogin());
    }


    @Test
    public void updateUserNotFoundLoginTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        createUser();

        var form = new Form();
        form.param("login", "newLogin");


        client.path("/user/1024/login").accept(MediaType.APPLICATION_JSON);

        var resp = client.put(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }

    @Test
    public void updateUserLoginExistsTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var form = new Form();
        form.param("login", usr.getLogin());


        client.path("/user/{id}/login",usr.getId()).accept(MediaType.APPLICATION_JSON);

        var resp = client.put(form);
        assertEquals(302, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_EXISTS, msg);
    }

    @Test
    public void updateUserPasswordTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var oldHash = usr.getPassword();
        var form = new Form();
        form.param("oldPassword", password);
        form.param("newPassword", "newPwd");


        client.path("/user/{id}/password",usr.getId()).accept(MediaType.APPLICATION_JSON);

        var resp = client.put(form);
        assertEquals(200, resp.getStatus());

        usr = resp.readEntity(User.class);
        assertNotEquals(oldHash, usr.getPassword());
    }

    @Test
    public void updateUserNotFoundPasswordTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        createUser();

        var form = new Form();
        form.param("oldPassword", password);
        form.param("newPassword", "newPwd");


        client.path("/user/1024/password").accept(MediaType.APPLICATION_JSON);

        var resp = client.put(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }


    @Test
    public void updateUserWrongPasswordTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var form = new Form();
        form.param("oldPassword", "blabla");
        form.param("newPassword", "newPwd");

        client.path("/user/{id}/password",usr.getId()).accept(MediaType.APPLICATION_JSON);

        var resp = client.put(form);
        assertEquals(401, resp.getStatus());

    }

    @Test
    public void findUserByLoginTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        var usr = createUser();

        client.path("/user/{login}",usr.getLogin()).accept(MediaType.APPLICATION_JSON);

        var resp = client.get();
        assertEquals(200, resp.getStatus());

        usr = resp.readEntity(User.class);
        assertEquals(10, usr.getId());
        assertEquals(login, usr.getLogin());
    }

    @Test
    public void findUserByLoginNotFoundTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        createUser();

        client.path("/user/test").accept(MediaType.APPLICATION_JSON);

        var resp = client.get();
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }

    @Test
    public void findUsersByIdsTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        var usr = createUser();

        client.path("/user").accept(MediaType.APPLICATION_JSON)
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
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        createUser();

        client.path("/user").accept(MediaType.APPLICATION_JSON)
                .query("id", 1L)
                .query("id", 2L);

        var resp = client.get();
        assertEquals(204, resp.getStatus());
}

    @Test
    public void resetPasswordTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        var usr = createUser();

        client.path("/user/{id}/reset_pwd",usr.getId()).accept(MediaType.APPLICATION_JSON);
        var resp = client.put(null);
        assertEquals(200, resp.getStatus());

    }

    @Test
    public void resetPasswordUserNotFoundTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        createUser();

        client.path("/user/7/reset_pwd").accept(MediaType.APPLICATION_JSON);

        var resp = client.put(null);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }

    @Test
    public void deleteUserTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        client.path("/user/{id}",usr.getId()).accept(MediaType.APPLICATION_JSON);

        var resp = client.delete();
        assertEquals(200, resp.getStatus());

        usr = resp.readEntity(User.class);
        assertEquals("deleted", usr.getLogin());

    }


    @Test
    public void deleteUserNotFoundTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        createUser();

        client.path("/user/1024").accept(MediaType.APPLICATION_JSON);

        var resp = client.delete();
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);

    }

    @Test
    public void deleteUseIsOwnerTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();
        createTestContributor(usr.getId());

        client.path("/user/{id}",usr.getId()).accept(MediaType.APPLICATION_JSON);

        var resp = client.delete();
        assertEquals(501, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_IS_PROJECT_OWNER, msg);

    }

    @Test
    public void loginUserTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

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
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

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
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

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
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        client.path("/user/{id}/logout",usr.getId()).accept(MediaType.APPLICATION_JSON);

        var resp = client.get();
        assertEquals(200, resp.getStatus());

        var token = resp.getCookies().get("X-JWT-AUTH").getValue();
        assertEquals("", token);

    }

    @Test
    public void logoutUserNotFoundTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        createUser();

        client.path("/user/1024/logout").accept(MediaType.APPLICATION_JSON);

        var resp = client.get();
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);

    }

    @Test
    public void enterCodeTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var form = new Form();
        form.param("code", "12345");

        client.path("/user/{userId}/code",usr.getId())
                .accept(MediaType.APPLICATION_JSON);

        var resp = client.post(form);
        assertEquals(200, resp.getStatus());


    }

    @Test
    public void enterCodeUserNotFoundTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        createUser();

        var form = new Form();
        form.param("code", "12345");

        client.path("/user/7/code")
                .accept(MediaType.APPLICATION_JSON);


        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);

    }

    @Test
    public void enterCodeNotMatchesTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var usr = createUser();

        var form = new Form();
        form.param("code", "123");

        client.path("/user/{userId}/code",usr.getId())
                .accept(MediaType.APPLICATION_JSON);


        var resp = client.post(form);
        assertEquals(400, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CODE_NOT_MATCHES, msg);
    }


    private User createUser() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/user").accept(MediaType.APPLICATION_JSON);

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
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/user/create_test_contributor").query("id", usrId);
        client.get();
        client.close();
    }

}
