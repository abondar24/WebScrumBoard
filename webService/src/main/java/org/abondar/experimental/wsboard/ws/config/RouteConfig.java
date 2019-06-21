package org.abondar.experimental.wsboard.ws.config;

import org.abondar.experimental.wsboard.ws.route.EmailRoute;
import org.abondar.experimental.wsboard.ws.route.UserEmailRoute;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteBuilder userEmailRoute() {
        return new UserEmailRoute();
    }

    @Bean
    public RouteBuilder emailRoute() {
        return new EmailRoute();
    }
}
