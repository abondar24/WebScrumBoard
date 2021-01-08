package org.abondar.experimental.wsboard.server.config;

import org.abondar.experimental.wsboard.server.dao.ContributorDao;
import org.abondar.experimental.wsboard.server.route.ContributorRoute;
import org.abondar.experimental.wsboard.server.route.EmailRoute;
import org.abondar.experimental.wsboard.server.route.ProjectRoute;
import org.abondar.experimental.wsboard.server.route.RestRoute;
import org.abondar.experimental.wsboard.server.route.SprintRoute;
import org.abondar.experimental.wsboard.server.route.TaskRoute;
import org.abondar.experimental.wsboard.server.route.UserRoute;

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
        return new RestRoute(messageSource);
    }

    @Bean
    public RouteBuilder userServiceRoute() {
        return new UserRoute();
    }

    @Bean
    public RouteBuilder emailRoute() {
        return new EmailRoute(messageSource);
    }

    @Bean
    public RouteBuilder projectServiceRoute() {
        return new ProjectRoute();
    }

    @Bean
    public RouteBuilder contributorServiceRoute() {
        return new ContributorRoute(contributorDao,messageSource);
    }

    @Bean
    public RouteBuilder taskServiceRoute() {
        return new TaskRoute();
    }

    @Bean
    public RouteBuilder sprintServiceRoute() {
        return new SprintRoute();
    }

}
