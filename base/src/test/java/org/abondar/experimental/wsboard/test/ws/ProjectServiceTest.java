package org.abondar.experimental.wsboard.test.ws;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.test.ws.impl.ProjectServiceTestImpl;
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
public class ProjectServiceTest {
    private Logger logger = LoggerFactory.getLogger(ProjectServiceTest.class);

    private static Server server;
    private static String endpoint = "local://wsboard_test_1";

    private String projectName = "project";
    private String startDate = "31/12/1122";

    @BeforeAll
    public static void beforeMethod() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        factory.setProvider(new JacksonJsonProvider());
        factory.setAddress(endpoint);
        factory.setServiceBean(new ProjectServiceTestImpl());
        server = factory.create();
        server.start();
    }

    @Test
    public void createProjectTest() {
        logger.info("create project test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", projectName);
        form.param("startDate", startDate);

        client.path("/project/create").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(200, res.getStatus());

        var prj = res.readEntity(Project.class);
        assertEquals(10, prj.getId());
    }

    @Test
    public void createProjectExistsTest() {
        logger.info("create project exists test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", "exists");
        form.param("startDate", startDate);

        client.path("/project/create").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(302, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_EXISTS, msg);
    }

    @Test
    public void createProjectBlankNameTest() {
        logger.info("create project blank name test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", "");
        form.param("startDate", startDate);

        client.path("/project/create").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(206, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.BLANK_DATA, msg);
    }

    @Test
    public void createProjectIncorrectDateTest() {
        logger.info("create project incorrect date test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", projectName);
        form.param("startDate", "blabla");

        client.path("/project/create").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        assertEquals(206, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_PARSE_DATE_FAILED, msg);
    }

    @Test
    public void deleteProjectTest() {
        logger.info("delete project test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        var prj = createProject();

        client.path("/project/delete").query("id", prj.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(200, res.getStatus());
    }

    @Test
    public void deleteProjectNotExistsTest() {
        logger.info("delete project not exists test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        createProject();

        client.path("/project/delete").query("id", "1000").accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);
    }

    @Test
    public void findProjectTest() {
        logger.info("find project test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        var prj = createProject();

        client.path("/project/find").query("id", prj.getId()).accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(200, res.getStatus());

        var resPrj = res.readEntity(Project.class);
        assertEquals(prj.getName(), resPrj.getName());
    }


    @Test
    public void findProjectNotExistsTest() {
        logger.info("find project not exists test");

        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));
        createProject();

        client.path("/project/find").query("id", "1000").accept(MediaType.APPLICATION_JSON);

        var res = client.get();
        assertEquals(404, res.getStatus());

        var msg = res.readEntity(String.class);
        assertEquals(LogMessageUtil.PROJECT_NOT_EXISTS, msg);
    }

    private Project createProject() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        var form = new Form();
        form.param("name", projectName);
        form.param("startDate", startDate);

        client.path("/project/create").accept(MediaType.APPLICATION_JSON);

        var res = client.post(form);
        return res.readEntity(Project.class);
    }
}
