package org.abondar.experimental.wsboard.test.webService;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.abondar.experimental.wsboard.test.webService.impl.AuthServiceTestImpl;
import org.abondar.experimental.wsboard.test.webService.impl.UserServiceTestImpl;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BaseServiceTest {

    protected static Server server;
    protected static String endpoint = "local://wsboard_test";

    protected String login = "login";
    protected String email = "email@email.com";
    protected String password = "pwd";
    protected String firstName = "fname";
    protected String lastName = "lname";
    protected String userRoles = UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name();


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

}
