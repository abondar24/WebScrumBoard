package org.abondar.experimental.wsboard.test.ws.impl;

import org.abondar.experimental.wsboard.ws.service.AuthService;

import java.util.List;

/**
 * Test implementation of authorization service
 */
public class AuthServiceTestImpl implements AuthService {


    @Override
    public String createToken(String login, String issuer, List<String> roles) {
        return "testToken";
    }

    @Override
    public boolean validateUser(String login, String password) {
        return false;
    }

    @Override
    public String authorizeUser(String login, String pwd) {
        return null;
    }


}
