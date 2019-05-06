package org.abondar.experimental.wsboard.webService.impl;

import org.abondar.experimental.wsboard.webService.service.AuthService;
import org.abondar.experimental.wsboard.webService.service.RestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class RestServiceImpl implements RestService {

    private AuthService authService;

    public RestServiceImpl(AuthService authService) {
        this.authService = authService;
    }

    @GET
    public void echo() {

    }
}
