package org.abondar.experimental.wsboard.email.service;

import org.apache.camel.CamelContext;

public class EmailService {

    private CamelContext context;

    public EmailService(CamelContext context) {
        this.context = context;
    }

    public void sendEmail(String recipient) {

    }
}
