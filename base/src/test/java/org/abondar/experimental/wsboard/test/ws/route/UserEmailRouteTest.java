package org.abondar.experimental.wsboard.test.ws.route;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.abondar.experimental.wsboard.test.ws.impl.AuthServiceTestImpl;
import org.abondar.experimental.wsboard.test.ws.impl.UserServiceTestImpl;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Map;

@SpringBootTest(classes = WebScrumBoardApplication.class)
@RunWith(CamelSpringBootRunner.class)
@ActiveProfiles("test")
@MockEndpoints
public class UserEmailRouteTest {

    @Autowired
    private ProducerTemplate producerTemplate;


    @EndpointInject(uri = "mock:sendEmail")
    private MockEndpoint mockEndpoint;

    private static String endpoint = "local://wsboard_camel";
    private String login = "login";
    private String email = "email@email.com";
    private String password = "pwd";
    private String firstName = "fname";
    private String lastName = "lname";
    private String userRoles = UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name();


    @BeforeClass
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
    public void createUserRouteTest() throws Exception {
        producerTemplate.sendBodyAndHeaders("direct:createUser", createUser(),
                Map.of("emailType", "createUser"));

        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "createUser");
        mockEndpoint.expectedMessageCount(1);


        mockEndpoint.reset();
    }

    @Test
    public void updateLoginRouteTest() throws Exception {

        producerTemplate.sendBodyAndHeaders("direct:updateLogin", createUser(),
                Map.of("emailType", "updateLogin"));
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "updateLogin");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void updatePasswordRouteTest() throws Exception {
        producerTemplate.sendBodyAndHeaders("direct:updatePassword", createUser(),
                Map.of("emailType", "updatePassword"));
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "updatePassword");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void deleteUserRouteTest() throws Exception {
        producerTemplate.sendBodyAndHeaders("direct:deleteUser", createUser(),
                Map.of("emailType", "deleteUser"));
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "deleteUser");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void resetPasswordRouteTest() throws Exception {
        producerTemplate.sendBodyAndHeaders("direct:resetPassword", createUser(),
                Map.of("emailType", "resetPassword"));
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "resetPassword");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    private Response createUser() {
        var client = WebClient.create(endpoint, Collections.singletonList(new JacksonJsonProvider()));

        client.path("/user/create").accept(MediaType.APPLICATION_JSON);

        var form = new Form();
        form.param("login", login);
        form.param("email", email);
        form.param("firstName", firstName);
        form.param("lastName", lastName);
        form.param("password", password);
        form.param("roles", userRoles);

        return client.post(form);

    }


}
