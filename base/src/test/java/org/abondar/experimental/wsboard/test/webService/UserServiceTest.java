package org.abondar.experimental.wsboard.test.webService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.dao.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.abondar.experimental.wsboard.test.webService.impl.AuthServiceTestImpl;
import org.abondar.experimental.wsboard.test.webService.impl.UserServiceTestImpl;
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

@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

    private Logger logger = LoggerFactory.getLogger(UserServiceTest.class);
    private static Server server;
    private static String endpoint = "local://wsboard_user_test";
    private ObjectMapper objectMapper = new ObjectMapper();

    private String login = "login";
    private String email = "email@email.com";
    private String password = "pwd";
    private String firstName = "fname";
    private String lastName = "lname";
    private String userRoles = UserRole.Developer.name() + ";" + UserRole.DevOps.name();


    @BeforeAll
    public static void beforeMethod() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
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

        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));


        client.path("/create").accept(MediaType.APPLICATION_JSON);

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


        client.path("/create").accept(MediaType.APPLICATION_JSON);

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
        assertEquals(ErrorMessageUtil.USER_EXISTS, err);


    }

    @Test
    public void createUserBlankDataTest() {
        logger.info("create user blank data test");

        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));


        client.path("/create").accept(MediaType.APPLICATION_JSON);

        Form form = new Form();
        form.param("login", login);
        form.param("email", email);
        form.param("firstName", firstName);
        form.param("lastName", null);
        form.param("password", password);
        form.param("roles", userRoles);

        var resp = client.post(form);
        assertEquals(501, resp.getStatus());

        var err = resp.readEntity(String.class);
        assertEquals(ErrorMessageUtil.BLANK_DATA, err);


    }

    @Test
    public void createUserNoRolesTest() {
        logger.info("create user no roles test");

        WebClient client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));


        client.path("/create").accept(MediaType.APPLICATION_JSON);

        Form form = new Form();
        form.param("login", login);
        form.param("email", email);
        form.param("firstName", firstName);
        form.param("lastName", lastName);
        form.param("password", password);
        form.param("roles", "salosalo");

        var resp = client.post(form);
        assertEquals(501, resp.getStatus());

        var err = resp.readEntity(String.class);
        assertEquals(ErrorMessageUtil.USER_NO_ROLES, err);


    }
}
