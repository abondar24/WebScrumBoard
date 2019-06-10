package org.abondar.experimental.wsboard.email.config;

import org.abondar.experimental.wsboard.email.service.EmailService;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfiguration {

    @Autowired
    private CamelContext camelContext;

    @Bean
    public EmailService emailService() {
        return new EmailService(camelContext);
    }
}
