package org.abondar.experimental.wsboard.test.webService.impl;

import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.ws.service.AuthService;

import java.util.Date;
import java.util.List;

/**
 * Test implementation of authorization service
 */
public class AuthServiceTestImpl implements AuthService {
    @Override
    public String renewToken(String tokenStr) {
        return null;
    }

    @Override
    public String getSubject(String token) {
        return null;
    }

    @Override
    public String createToken(String login, String issuer, List<String> roles) {
        return "testToken";
    }

    @Override
    public boolean validateUser(Long userId, String password) {
        return false;
    }

    @Override
    public String authorizeUser(User user, String pwd) {
        return null;
    }

    @Override
    public void logRecord(Date logDate, String action, String subject, String record) {

    }
}
