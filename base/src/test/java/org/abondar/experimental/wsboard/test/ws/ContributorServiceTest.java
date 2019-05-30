package org.abondar.experimental.wsboard.test.ws;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.abondar.experimental.wsboard.test.ws.impl.ContributorServiceTestImpl;
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
public class ContributorServiceTest {
    private Logger logger = LoggerFactory.getLogger(ContributorServiceTest.class);

    private static Server server;
    private static String endpoint = "local://wsboard_test_3";

    @BeforeAll
    public static void beforeMethod() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        factory.setProvider(new JacksonJsonProvider());
        factory.setAddress(endpoint);
        factory.setServiceBean(new ContributorServiceTestImpl());
        server = factory.create();
        server.start();
    }

    @Test
    public void createContributorTest() {
        logger.info("create contributor test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/create").accept(MediaType.APPLICATION_JSON);

        var userId = createUser();
        var projectId = createProject();

        var form = new Form();

        form.param("userId", String.valueOf(userId));
        form.param("projectId", String.valueOf(projectId));
        form.param("isOwner", "false");

        var resp = client.post(form);
        assertEquals(200, resp.getStatus());

        var ctr = resp.readEntity(Contributor.class);
        assertEquals(10, ctr.getId());

    }

    @Test
    public void createContributorUserNotFoundTest() {
        logger.info("create contributor user not found test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/create").accept(MediaType.APPLICATION_JSON);

        createUser();
        var projectId = createProject();

        var form = new Form();

        form.param("userId", "7");
        form.param("projectId", String.valueOf(projectId));
        form.param("isOwner", "false");

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);

    }

    @Test
    public void createContributorProjectNotFoundTest() {
        logger.info("create contributor project not found test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/create").accept(MediaType.APPLICATION_JSON);

        var userId = createUser();
        createProject();

        var form = new Form();

        form.param("userId", String.valueOf(userId));
        form.param("projectId", "7");
        form.param("isOwner", "false");

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);

    }

    @Test
    public void createContributorProjectNotActiveTest() {
        logger.info("create contributor project not active test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/create").accept(MediaType.APPLICATION_JSON);

        var userId = createUser();
        var projectId = createProject();
        updateProject();

        var form = new Form();

        form.param("userId", String.valueOf(userId));
        form.param("projectId", String.valueOf(projectId));
        form.param("isOwner", "false");

        var resp = client.post(form);
        assertEquals(301, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_ACTIVE, msg);

    }

    @Test
    public void createContributorProjectHasOwnerTest() {
        logger.info("create contributor project has owner test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/create").accept(MediaType.APPLICATION_JSON);

        var userId = createUser();
        var projectId = createProject();

        var form = new Form();

        form.param("userId", String.valueOf(userId));
        form.param("projectId", String.valueOf(projectId));
        form.param("isOwner", "true");

        var resp = client.post(form);
        assertEquals(409, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_HAS_OWNER, msg);

    }

    private Contributor createContributor(long userId, long projectId) {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/create").accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("userId", String.valueOf(userId));
        form.param("projectId", String.valueOf(projectId));
        form.param("isOwner", "false");

        var resp = client.post(form);

        return resp.readEntity(Contributor.class);
    }


    private long createUser() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/contributor/create_user").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("login", "login");
        form.param("email", "email");
        form.param("firstName", "First");
        form.param("lastName", "Last");
        form.param("password", "pass");
        form.param("roles", UserRole.DEVELOPER.name());


        var resp = client.post(form);

        return resp.readEntity(User.class).getId();
    }

    private long createProject() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", "prjName");
        form.param("startDate", "31/10/1999");

        client.path("/contributor/create_project").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        return res.readEntity(Project.class).getId();
    }

    private void updateProject() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("isActive", "false");

        client.path("/contributor/update_project").accept(MediaType.APPLICATION_JSON);

        client.post(form);
    }

}
