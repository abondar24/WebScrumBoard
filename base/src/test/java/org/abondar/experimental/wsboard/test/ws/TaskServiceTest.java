package org.abondar.experimental.wsboard.test.ws;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.test.ws.impl.AuthServiceTestImpl;
import org.abondar.experimental.wsboard.test.ws.impl.ContributorServiceTestImpl;
import org.abondar.experimental.wsboard.test.ws.impl.ProjectServiceTestImpl;
import org.abondar.experimental.wsboard.test.ws.impl.SprintServiceTestImpl;
import org.abondar.experimental.wsboard.test.ws.impl.TaskServiceTestImpl;
import org.abondar.experimental.wsboard.test.ws.impl.UserServiceTestImpl;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class TaskServiceTest {
    private Logger logger = LoggerFactory.getLogger(TaskServiceTest.class);

    private static Server server;
    private static String endpoint = "local://wsboard_test_4";

    @BeforeAll
    public static void beforeMethod() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBindingId(JAXRSBindingFactory.JAXRS_BINDING_ID);
        factory.setProvider(new JacksonJsonProvider());
        factory.setAddress(endpoint);
        factory.setServiceBeanObjects(new TaskServiceTestImpl(),
                new ContributorServiceTestImpl(),
                new UserServiceTestImpl(new AuthServiceTestImpl()),
                new ProjectServiceTestImpl(),
                new SprintServiceTestImpl());
        server = factory.create();
        server.start();
    }
}