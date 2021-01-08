package org.abondar.experimental.wsboard.server.config;

import org.abondar.experimental.wsboard.server.dao.ContributorDao;
import org.abondar.experimental.wsboard.server.dao.ProjectDao;
import org.abondar.experimental.wsboard.server.dao.SecurityCodeDao;
import org.abondar.experimental.wsboard.server.dao.SprintDao;
import org.abondar.experimental.wsboard.server.dao.TaskDao;
import org.abondar.experimental.wsboard.server.dao.UserDao;
import org.abondar.experimental.wsboard.server.route.ContributorRoute;
import org.abondar.experimental.wsboard.server.route.EmailRoute;
import org.abondar.experimental.wsboard.server.route.ProjectRoute;
import org.abondar.experimental.wsboard.server.route.RestRoute;
import org.abondar.experimental.wsboard.server.route.SprintRoute;
import org.abondar.experimental.wsboard.server.route.TaskRoute;
import org.abondar.experimental.wsboard.server.route.UserRoute;
import org.abondar.experimental.wsboard.server.service.AuthServiceImpl;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Camel routes configuration class
 *
 * @author a.bondar
 */
@Configuration
@Import({CxfConfig.class,ContributorDao.class, UserDao.class, AuthServiceImpl.class,
        SecurityCodeDao.class, ProjectDao.class, TaskDao.class, SprintDao.class})
public class RouteConfig {

    @Autowired
    ContributorDao contributorDao;

    @Autowired
    MessageSource messageSource;

    @Autowired
    UserDao userDao;

    @Autowired
    AuthServiceImpl authService;

    @Autowired
    SecurityCodeDao securityCodeDao;

    @Autowired
    ProjectDao projectDao;

    @Autowired
    SprintDao sprintDao;

    @Autowired
    TaskDao taskDao;

    @Bean
    public RouteBuilder restServiceRoute() {
        return new RestRoute(messageSource);
    }

    @Bean
    public RouteBuilder userServiceRoute() {
        return new UserRoute(userDao,securityCodeDao,authService,messageSource);
    }

    @Bean
    public RouteBuilder emailRoute() {
        return new EmailRoute(messageSource);
    }

    @Bean
    public RouteBuilder projectServiceRoute() {
        return new ProjectRoute(projectDao,messageSource);
    }

    @Bean
    public RouteBuilder contributorServiceRoute() {
        return new ContributorRoute(contributorDao, messageSource);
    }

    @Bean
    public RouteBuilder taskServiceRoute() {
        return new TaskRoute(taskDao,messageSource);
    }

    @Bean
    public RouteBuilder sprintServiceRoute() {
        return new SprintRoute(sprintDao,messageSource);
    }

}
