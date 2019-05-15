package org.abondar.experimental.wsboard.test.webService.impl;

import org.abondar.experimental.wsboard.dao.exception.InvalidPasswordException;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.webService.service.AuthService;

import java.util.Date;
import java.util.List;

public class AuthServiceTestImpl implements AuthService {
    @Override
    public String renewToken(String tokenStr) throws Exception {
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
    public boolean validateUser(Long userId, String password) throws InvalidPasswordException {
        return false;
    }

    @Override
    public String authorizeUser(User user, String pwd) throws InvalidPasswordException {
        return null;
    }

    @Override
    public void logRecord(Date logDate, String action, String subject, String record) {

    }
}
