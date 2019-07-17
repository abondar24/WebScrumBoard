package org.abondar.experimental.wsboard.test.ws.route;

import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.cxf.message.MessageContentsList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

@SpringBootTest(classes = WebScrumBoardApplication.class)
@RunWith(CamelSpringBootRunner.class)
@ActiveProfiles("test")
@MockEndpoints
public class UserServiceRouteTest {

    @Autowired
    private ProducerTemplate producerTemplate;


    @EndpointInject(uri = "mock:sendEmail")
    private MockEndpoint mockEndpoint;

    private long userId = 7L;
    private String login = "login";
    private String email = "email@email.com";
    private String password = "pwd";
    private String firstName = "fname";
    private String lastName = "lname";
    private String userRoles = UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name();



    @Test
    public void createUserRouteTest() throws Exception {
        Object[] values = new Object[]{login, password, email, firstName, lastName, userRoles};
        MessageContentsList testList = new MessageContentsList(values);

        producerTemplate.sendBodyAndHeaders("direct:createUser", testList,
                Map.of("emailType", "createUser"));

        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "createUser");
        mockEndpoint.expectedMessageCount(1);


        mockEndpoint.reset();
    }

    @Test
    public void updateUserRouteTest() throws Exception {
        Object[] values = new Object[]{userId, firstName, lastName, email, userRoles};
        MessageContentsList testList = new MessageContentsList(values);

        producerTemplate.sendBodyAndHeaders("direct:updateUser", testList, Map.of());

        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);


        mockEndpoint.reset();
    }

    @Test
    public void updateAvatarRouteTest() throws Exception {
        Object[] values = new Object[]{userId, new byte[24]};
        MessageContentsList testList = new MessageContentsList(values);

        producerTemplate.sendBodyAndHeaders("direct:updateAvatar", testList, Map.of());

        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);


        mockEndpoint.reset();
    }

    @Test
    public void updateLoginRouteTest() throws Exception {
        Object[] values = new Object[]{login, userId};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:updateLogin", testList,
                Map.of("emailType", "updateLogin"));
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "updateLogin");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void updatePasswordRouteTest() throws Exception {
        Object[] values = new Object[]{password, "newPass", userId};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:updatePassword", testList,
                Map.of("emailType", "updatePassword"));
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "updatePassword");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void deleteUserRouteTest() throws Exception {
        Object[] values = new Object[]{userId};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:deleteUser", testList,
                Map.of("emailType", "deleteUser"));
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "deleteUser");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void loginRouteTest() throws Exception {
        Object[] values = new Object[]{login, password};
        MessageContentsList testList = new MessageContentsList(values);

        producerTemplate.sendBodyAndHeaders("direct:loginUser", testList, Map.of());

        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);


        mockEndpoint.reset();
    }

    @Test
    public void logoutRouteTest() throws Exception {
        Object[] values = new Object[]{userId};
        MessageContentsList testList = new MessageContentsList(values);

        producerTemplate.sendBodyAndHeaders("direct:logoutUser", testList, Map.of());

        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);


        mockEndpoint.reset();
    }


    @Test
    public void findUserByLoginTest() throws Exception {
        Object[] values = new Object[]{login};
        MessageContentsList testList = new MessageContentsList(values);

        producerTemplate.sendBodyAndHeaders("direct:findUserByLogin", testList, Map.of());

        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);


        mockEndpoint.reset();
    }

    @Test
    public void findUsersByIdsTest() throws Exception {
        Object[] values = new Object[]{List.of(1L,2L,3L)};
        MessageContentsList testList = new MessageContentsList(values);

        producerTemplate.sendBodyAndHeaders("direct:findUsersByIds", testList, Map.of());

        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);


        mockEndpoint.reset();
    }


    @Test
    public void resetPasswordRouteTest() throws Exception {
        Object[] values = new Object[]{userId};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:resetPassword", testList,
                Map.of("emailType", "resetPassword"));
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedHeaderValuesReceivedInAnyOrder("emailType", "resetPassword");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }

    @Test
    public void enterCodedRouteTest() throws Exception {
        Object[] values = new Object[]{userId, 24L};
        MessageContentsList testList = new MessageContentsList(values);
        producerTemplate.sendBodyAndHeaders("direct:enterCode", testList,
                Map.of());
        mockEndpoint.assertIsSatisfied();
        mockEndpoint.expectedBodiesReceived();
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.reset();
    }


}
