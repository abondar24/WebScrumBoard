package org.abondar.experimental.wsboard.base.config;

import org.abondar.experimental.wsboard.base.dao.ContributorDao;
import org.abondar.experimental.wsboard.route.ContributorServiceRoute;
import org.abondar.experimental.wsboard.route.EmailRoute;
import org.abondar.experimental.wsboard.route.ProjectServiceRoute;
import org.abondar.experimental.wsboard.route.RestServiceRoute;
import org.abondar.experimental.wsboard.route.SprintServiceRoute;
import org.abondar.experimental.wsboard.route.TaskServiceRoute;
import org.abondar.experimental.wsboard.route.UserServiceRoute;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Import({CxfConfig.class})
public class RouteConfig {

    @Autowired
    @Qualifier("contributorDao")
    ContributorDao contributorDao;

    @Autowired
    MessageSource messageSource;

    @Bean
    public RouteBuilder restServiceRoute() {
        return new RestServiceRoute(messageSource);
    }

    @Bean
    public RouteBuilder userServiceRoute() {
        return new UserServiceRoute();
    }

    @Bean
    public RouteBuilder emailRoute() {
        return new EmailRoute(messageSource);
    }

    @Bean
    public RouteBuilder projectServiceRoute() {
        return new ProjectServiceRoute();
    }

    @Bean
    public RouteBuilder contributorServiceRoute() {
        return new ContributorServiceRoute(contributorDao,messageSource);
    }

    @Bean
    public RouteBuilder taskServiceRoute() {
        return new TaskServiceRoute();
    }

    @Bean
    public RouteBuilder sprintServiceRoute() {
        return new SprintServiceRoute();
    }

}
