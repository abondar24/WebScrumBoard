package org.abondar.experimental.wsboard.ws.config;

import org.abondar.experimental.wsboard.ws.route.ContributorServiceRoute;
import org.abondar.experimental.wsboard.ws.route.EmailRoute;
import org.abondar.experimental.wsboard.ws.route.ProjectServiceRoute;
import org.abondar.experimental.wsboard.ws.route.RestServiceRoute;
import org.abondar.experimental.wsboard.ws.route.SprintServiceRoute;
import org.abondar.experimental.wsboard.ws.route.TaskServiceRoute;
import org.abondar.experimental.wsboard.ws.route.UserServiceRoute;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Camel routes configuration class
 *
 * @author a.bondar
 */
@Configuration
public class RouteConfig {

    @Bean
    public RouteBuilder restServiceRoute() {
        return new RestServiceRoute();
    }

    @Bean
    public RouteBuilder userServiceRoute() {
        return new UserServiceRoute();
    }

    @Bean
    public RouteBuilder emailRoute() {
        return new EmailRoute();
    }

    @Bean
    public RouteBuilder projectServiceRoute() {
        return new ProjectServiceRoute();
    }

    @Bean
    public RouteBuilder contributorServiceRoute() {
        return new ContributorServiceRoute();
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
