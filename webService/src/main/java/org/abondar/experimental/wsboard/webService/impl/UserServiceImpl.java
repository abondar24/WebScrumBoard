package org.abondar.experimental.wsboard.webService.impl;

import org.abondar.experimental.wsboard.dao.UserDao;
import org.abondar.experimental.wsboard.webService.service.AuthService;
import org.abondar.experimental.wsboard.webService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/user")
public class UserServiceImpl implements UserService {

    @Autowired
    @Qualifier("userDao")
    private UserDao dao;

    private AuthService authService;

    public UserServiceImpl(AuthService authService) {
        this.authService = authService;
    }

    @GET
    @Path("/echo")
    public void echo() {

    }
}
