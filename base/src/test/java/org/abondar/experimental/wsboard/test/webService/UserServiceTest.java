package org.abondar.experimental.wsboard.test.webService;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class UserServiceTest extends BaseServiceTest {

    private Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @Test
    public void createUserTest() {
        logger.info("create user test");

        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));


        client.path("/user/create").accept(MediaType.APPLICATION_JSON);

        Form form = new Form();
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
        logger.info("create user login exists test");

        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));


        client.path("/user/create").accept(MediaType.APPLICATION_JSON);

        Form form = new Form();
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

        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));


        client.path("/user/create").accept(MediaType.APPLICATION_JSON);

        Form form = new Form();
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

        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));


        client.path("/user/create").accept(MediaType.APPLICATION_JSON);

        Form form = new Form();
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
}
