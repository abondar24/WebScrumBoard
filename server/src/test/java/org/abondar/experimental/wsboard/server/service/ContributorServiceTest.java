package org.abondar.experimental.wsboard.server.service;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.server.datamodel.Contributor;
import org.abondar.experimental.wsboard.server.datamodel.Project;

import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.abondar.experimental.wsboard.server.datamodel.user.UserRole;
import org.abondar.experimental.wsboard.server.service.impl.ContributorServiceTestImpl;
import org.abondar.experimental.wsboard.server.util.LogMessageUtil;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ContributorServiceTest {
    private static final String ENDPOINT = "local://wsboard_test_3";

    @BeforeAll
    public static void beforeMethod() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        factory.setProvider(new JacksonJsonProvider());
        factory.setAddress(ENDPOINT);
        factory.setServiceBean(new ContributorServiceTestImpl());
        Server server = factory.create();
        server.start();
    }

    @Test
    public void createContributorTest() {
        deleteContributor();
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));


        var userId = createUser();
        var projectId = createProject();
        client.path("/contributor/user/{usrId}/project/{prjId}",userId,projectId).accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("isOwner", "false");

        var resp = client.post(form);
        assertEquals(200, resp.getStatus());

        var ctr = resp.readEntity(Contributor.class);
        assertEquals(10, ctr.getId());

    }

    @Test
    public void createContributorUserNotFoundTest() {
        deleteContributor();
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        createUser();
        var projectId = createProject();

        var form = new Form();
        client.path("/contributor/user/7/project/{prjId}",projectId).accept(MediaType.APPLICATION_JSON);

        form.param("isOwner", "false");

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);

    }

    @Test
    public void createContributorProjectNotFoundTest() {
        deleteContributor();
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        var userId = createUser();
        createProject();

        client.path("/contributor/user/{usrId}/project/7",userId).accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("isOwner", "false");

        var resp = client.post(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);

    }

    @Test
    public void createContributorProjectNotActiveTest() {
        deleteContributor();
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));


        var userId = createUser();
        var projectId = createProject();
        updateProject();
        client.path("/contributor/user/{usrId}/project/{prjId}",userId,projectId).accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("isOwner", "false");

        var resp = client.post(form);
        assertEquals(301, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_ACTIVE, msg);

    }

    @Test
    public void createContributorAlreadyExistsTest() {
        deleteContributor();
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");
        client.path("/contributor/user/{usrId}/project/{prjId}",userId,projectId).accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("isOwner", "false");

        var resp = client.post(form);
        assertEquals(302, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_EXISTS_LOG, msg);

    }

    @Test
    public void createContributorProjectHasOwnerTest() {
        deleteContributor();
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "true");
        client.path("/contributor/user/{usrId}/project/{prjId}",userId,projectId).accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("isOwner", "true");

        var resp = client.post(form);
        assertEquals(409, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_IS_ALREADY_OWNER, msg);

        deleteContributor();

    }


    @Test
    public void updateContributorTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/user/{usrId}/project/{prjId}",userId,projectId).accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("isActive", "true");
        form.param("isOwner", "true");

        var resp = client.put(form);
        assertEquals(200, resp.getStatus());

        ctr = resp.readEntity(Contributor.class);
        assertTrue(ctr.isOwner());
        assertTrue(ctr.isActive());

    }

    @Test
    public void updateContributorNoUserTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/user/100/project/{prjId}",projectId).accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("isActive", "true");
        form.param("isOwner", "true");

        var resp = client.put(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);

    }


    @Test
    public void updateContributorNoProjectTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/user/{usrId}/project/100",userId).accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("isActive", "true");
        form.param("isOwner", "true");

        var resp = client.put(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);

    }

    @Test
    public void updateContributorNoCtrTest() {
        var userId = createUser();
        var projectId = createProject();
        deleteContributor();

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/user/{usrId}/project/{prjId}",userId,projectId).accept(MediaType.APPLICATION_JSON);


        var form = new Form();

        form.param("isActive", "true");
        form.param("isOwner", "true");

        var resp = client.put(form);
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS, msg);

    }

    @Test
    public void updateContributorOwnerFoundTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "true");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/user/{usrId}/project/{prjId}",userId,projectId).accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("isOwner", "true");

        var resp = client.put(form);
        assertEquals(302, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_IS_ALREADY_OWNER, msg);

    }


    @Test
    public void updateContributorNoOwnerTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/user/{usrId}/project/{prjId}",userId,projectId).accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("isOwner", "false");

        var resp = client.put(form);
        assertEquals(409, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_HAS_NO_OWNER, msg);

    }


    @Test
    public void updateNotActiveContributorAsOwnerTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");
        deactivateContributor(userId,projectId);

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/user/{usrId}/project/{prjId}",userId,projectId).accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("isOwner", "true");

        var resp = client.put(form);
        assertEquals(410, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_NOT_ACTIVE, msg);

    }


    @Test
    public void updateContributorDeactivateOwnerTest() {
        var userId = createUser();
        var projectId = createProject();
         createContributor(userId, projectId, "true");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/user/{usrId}/project/{prjId}",userId,projectId).accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("isActive", "false");

        var resp = client.put(form);
        assertEquals(403, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_CANNOT_BE_DEACTIVATED, msg);

    }

    @Test
    public void findProjectOwnerTest() {
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "true");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/project/{projectId}/owner",projectId).accept(MediaType.APPLICATION_JSON);

        var resp = client.get();
        assertEquals(200, resp.getStatus());

        var ctr = resp.readEntity(Contributor.class);
        assertEquals(10, ctr.getId());

        deleteContributor();

    }


    @Test
    public void findProjectOwnerProjectNotFoundTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/project/{projectId}/owner",7).accept(MediaType.APPLICATION_JSON);

        var resp = client.get();
        assertEquals(404, resp.getStatus());

        var msg = resp.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);

    }


    @Test
    public void findProjectOwnerProjectHasNoOwnerTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/project/{projectId}/owner",projectId).accept(MediaType.APPLICATION_JSON);

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

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/project/{projectId}",projectId)
                .accept(MediaType.APPLICATION_JSON)
                .query("offset", 0)
                .query("limit", 2);

        var res = client.get();
        assertEquals(200, res.getStatus());

        Collection<? extends Contributor> ctrs = client.getCollection(Contributor.class);
        assertEquals(2, ctrs.size());
        assertEquals(ctr.getId(), ctrs.iterator().next().getId());


    }

    @Test
    public void countContributorsTest() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/project/{projectId}/count",7)
                .accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(200, res.getStatus());

        var count = res.readEntity(Integer.class);
        assertEquals(Integer.valueOf(7), count);

    }

    @Test
    public void findContributorsProjectNotFoundTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/project/{projectId}",7)
                .accept(MediaType.APPLICATION_JSON)
                .query("offset", 0)
                .query("limit", 2);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);

    }

    @Test
    public void findContributorsNegativeOffsetTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/project/{projectId}",projectId)
                .accept(MediaType.APPLICATION_JSON)
                .query("offset", -1)
                .query("limit", 2);

        var res = client.get();
        assertEquals(200, res.getStatus());

        Collection<? extends Contributor> ctrs = client.getCollection(Contributor.class);
        assertEquals(5, ctrs.size());

    }

    @Test
    public void findContributorsEmptyResTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/project/{projectId}",projectId)
                .accept(MediaType.APPLICATION_JSON)
                .query("offset", 7)
                .query("limit", 2);

        var res = client.get();
        assertEquals(204, res.getStatus());

    }

    @Test
    public void findContributorsByUserIdTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/user/{userId}",userId)
                .accept(MediaType.APPLICATION_JSON)
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
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/user/7")
                .accept(MediaType.APPLICATION_JSON)
                .query("offset", 0)
                .query("limit", 3);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }

    @Test
    public void findContributorsByUserEmptyTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/user/{userId}",userId)
                .accept(MediaType.APPLICATION_JSON)
                .query("offset", 7)
                .query("limit", 3);

        var res = client.get();
        assertEquals(204, res.getStatus());

    }

    @Test
    public void findProjectContributorTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/project/{projectId}/user/{userId}",projectId,userId)
                .accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(200, res.getStatus());

        var ctrId = res.readEntity(Long.class);
        assertEquals(Long.valueOf(ctr.getId()),ctrId);
    }

    @Test
    public void findProjectContributorUserNotFoundTest() {
        var userId = createUser();
        var projectId = createProject();
        createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/project/{projectId}/user/{userId}",projectId,7)
                .accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.USER_NOT_EXISTS, msg);
    }

    @Test
    public void findProjectContributorProjectNotFoundTest() {
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/project/{projectId}/user/{userId}",100,userId)
                .accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);
    }

    @Test
    public void findContributorByNameTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/project/{projectId}/login/{login}",projectId,"login")
                .accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(200, res.getStatus());

        var ctrId = res.readEntity(Contributor.class).getId();
        assertEquals(ctr.getId(),ctrId);
    }

    @Test
    public void findContributorNotExistsByNameTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/project/{projectId}/login/{login}",projectId,"test")
                .accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS, msg);
    }

    @Test
    public void findContributorByNameProjectNotExistsTest() {
        deleteContributor();
        var userId = createUser();
        var projectId = createProject();
        var ctr = createContributor(userId, projectId, "false");

        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/project/{projectId}/login/{login}",100,"login")
                .accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);
    }

    private Contributor createContributor(long userId, long projectId, String isOwner) {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/user/{usrId}/project/{prjId}",projectId,userId).accept(MediaType.APPLICATION_JSON);

        var form = new Form();

        form.param("isOwner", isOwner);

        var resp = client.post(form);

        return resp.readEntity(Contributor.class);
    }


    private long createUser() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/contributor/user").accept(MediaType.APPLICATION_JSON);

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
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", "prjName");
        form.param("startDate", "31/10/1999");

        client.path("/contributor/project").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        return res.readEntity(Project.class).getId();
    }

    private void updateProject() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("isActive", "false");

        client.path("/contributor/project").accept(MediaType.APPLICATION_JSON).put(form);
    }

    private void deleteContributor() {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));


        client.path("/contributor").accept(MediaType.APPLICATION_JSON).delete();
    }


    private void deactivateContributor(long userId,long projectId) {
        var client = WebClient.create(ENDPOINT, Collections.singletonList(new JacksonJsonProvider()));
        client.path("/contributor/user/{usrId}/project/{prjId}",userId,projectId).accept(MediaType.APPLICATION_JSON);
        var form = new Form();

        form.param("isActive", "false");

        client.put(form);
    }

}
