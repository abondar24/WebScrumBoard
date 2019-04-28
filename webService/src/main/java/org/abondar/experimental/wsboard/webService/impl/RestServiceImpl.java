package org.abondar.experimental.wsboard.webService.impl;

import org.abondar.experimental.wsboard.webService.service.AuthService;

public class RestServiceImpl {

    private AuthService authService;

    public RestServiceImpl(AuthService authService) {
        this.authService = authService;
    }
}
