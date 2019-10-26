package org.abondar.experimental.wsboard.test.ws;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = WebScrumBoardApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ContributorServiceTest {
    private static String endpoint = "local://wsboard_test_3";

    @BeforeAll
    public static void beforeMethod() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        factory.setProvider(new JacksonJsonProvider());
        factory.setAddress(endpoint);
        factory.setServiceBean(new ContributorServiceTestImpl());
        Server server = factory.create();
        server.start();
    }

    @Test
    public void createContributorTest() {
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
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/create").accept(MediaType.APPLICATION_JSON);

        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "true");

        var form = new Form();

        form.param("userId", String.valueOf(userId));
        form.param("projectId", String.valueOf(projectId));
        form.param("isOwner", "true");

        var resp = client.post(form);
        assertEquals(409, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_IS_ALREADY_OWNER, msg);

        deleteContributor();

    }


    @Test
    public void updateContributorTest() {
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/update").accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("usrId", String.valueOf(userId));
        form.param("prjId",String.valueOf(projectId));
        form.param("isActive", "true");
        form.param("isOwner", "true");

        var resp = client.post(form);
        assertEquals(200, resp.getStatus());

        ctr = resp.readEntity(Contributor.class);
        assertTrue(ctr.isOwner());
        assertTrue(ctr.isActive());

    }

    @Test
    public void updateContributorNoUserTest() {
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/update").accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("usrId", "100");
        form.param("prjId",String.valueOf(projectId));
        form.param("isActive", "true");
        form.param("isOwner", "true");

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);

    }


    @Test
    public void updateContributorNoProjectTest() {
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/update").accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("usrId", String.valueOf(userId));
        form.param("prjId","100");
        form.param("isActive", "true");
        form.param("isOwner", "true");

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);

    }

    @Test
    public void updateContributorNoCtrTest() {
        var userId = createUser();
        var projectId = createProject();
        deleteContributor();

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/update").accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("usrId", String.valueOf(userId));
        form.param("prjId",String.valueOf(projectId));
        form.param("isActive", "true");
        form.param("isOwner", "true");

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS, msg);

    }

    @Test
    public void updateContributorOwnerFoundTest() {
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "true");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/update").accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("usrId", String.valueOf(userId));
        form.param("prjId",String.valueOf(projectId));
        form.param("isOwner", "true");

        var resp = client.post(form);
        assertEquals(302, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_IS_ALREADY_OWNER, msg);

    }


    @Test
    public void updateContributorNoOwnerTest() {
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/update").accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("usrId", String.valueOf(userId));
        form.param("prjId",String.valueOf(projectId));
        form.param("isOwner", "false");

        var resp = client.post(form);
        assertEquals(409, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_HAS_NO_OWNER, msg);

    }


    @Test
    public void updateNotActiveContributorAsOwnerTest() {
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");
        deactivateContributor(userId,projectId);

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/update").accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("usrId", String.valueOf(userId));
        form.param("prjId",String.valueOf(projectId));
        form.param("isOwner", "true");

        var resp = client.post(form);
        assertEquals(410, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_NOT_ACTIVE, msg);

    }


    @Test
    public void updateContributorDeactivateOwnerTest() {
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "true");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/update").accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("usrId", String.valueOf(userId));
        form.param("prjId",String.valueOf(projectId));
        form.param("isActive", "false");

        var resp = client.post(form);
        assertEquals(403, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_CANNOT_BE_DEACTIVATED, msg);

    }

    @Test
    public void findProjectOwnerTest() {
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "true");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/find_project_owner").accept(MediaType.APPLICATION_JSON).query("projectId", projectId);

        var resp = client.get();
        assertEquals(200, resp.getStatus());

        var ctr = resp.readEntity(Contributor.class);
        assertEquals(10, ctr.getId());

        deleteContributor();

    }


    @Test
    public void findProjectOwnerProjectNotFoundTest() {
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/find_project_owner").accept(MediaType.APPLICATION_JSON).query("projectId", 7);

        var resp = client.get();
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);

    }


    @Test
    public void findProjectOwnerProjectHasNoOwnerTest() {
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/find_project_owner").accept(MediaType.APPLICATION_JSON).query("projectId", projectId);

        var resp = client.get();
        assertEquals(204, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_HAS_NO_OWNER, msg);

    }

    @Test
    public void findContributorsTest() {
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/find_project_contributors")
                .accept(MediaType.APPLICATION_JSON)
                .query("projectId", projectId)
                .query("offset", 0)
                .query("limit", 2);

        var res = client.get();
        assertEquals(200, res.getStatus());

        Collection<? extends Contributor> ctrs = client.getCollection(Contributor.class);
        assertEquals(2, ctrs.size());
        assertEquals(ctr.getId(), ctrs.iterator().next().getId());


    }

    @Test
    public void findContributorsProjectNotFoundTest() {
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/find_project_contributors")
                .accept(MediaType.APPLICATION_JSON)
                .query("projectId", 7)
                .query("offset", 0)
                .query("limit", 2);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);

    }

    @Test
    public void findContributorsNegativeOffsetTest() {
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/find_project_contributors")
                .accept(MediaType.APPLICATION_JSON)
                .query("projectId", projectId)
                .query("offset", -1)
                .query("limit", 2);

        var res = client.get();
        assertEquals(200, res.getStatus());

        Collection<? extends Contributor> ctrs = client.getCollection(Contributor.class);
        assertEquals(5, ctrs.size());

    }

    @Test
    public void findContributorsEmptyResTest() {
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/find_project_contributors")
                .accept(MediaType.APPLICATION_JSON)
                .query("projectId", projectId)
                .query("offset", 7)
                .query("limit", 2);

        var res = client.get();
        assertEquals(204, res.getStatus());

    }

    @Test
    public void findContributorsByUserIdTest() {
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/find_contributors_by_user")
                .accept(MediaType.APPLICATION_JSON)
                .query("userId", userId)
                .query("offset", 0)
                .query("limit", 3);

        var res = client.get();
        assertEquals(200, res.getStatus());

        Collection<? extends Contributor> ctrs = client.getCollection(Contributor.class);
        assertEquals(3,ctrs.size());
        assertEquals(ctr.getId(),ctrs.iterator().next().getId());
    }

    @Test
    public void findContributorsByUserNotFoundTest() {
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/find_contributors_by_user")
                .accept(MediaType.APPLICATION_JSON)
                .query("userId", 7)
                .query("offset", 0)
                .query("limit", 3);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }

    @Test
    public void findContributorsByUserEmptyTest() {
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/find_contributors_by_user")
                .accept(MediaType.APPLICATION_JSON)
                .query("userId", userId)
                .query("offset", 7)
                .query("limit", 3);

        var res = client.get();
        assertEquals(204, res.getStatus());

    }

    private Contributor createContributor(long userId, long projectId, String isOwner) {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/create").accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("userId", String.valueOf(userId));
        form.param("projectId", String.valueOf(projectId));
        form.param("isOwner", isOwner);

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

        client.path("/contributor/update_project").accept(MediaType.APPLICATION_JSON).post(form);
    }

    private void deleteContributor() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));


        client.path("/contributor/delete_contributor").accept(MediaType.APPLICATION_JSON).get();
    }


    private void deactivateContributor(long userId,long projectId) {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/update").accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("usrId", String.valueOf(userId));
        form.param("prjId",String.valueOf(projectId));
        form.param("isActive", "false");

        client.post(form);
    }

}
