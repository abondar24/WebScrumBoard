package org.abondar.experimental.wsboard.webService.service;

import org.abondar.experimental.wsboard.dao.password.exception.InvalidPasswordException;
import org.abondar.experimental.wsboard.datamodel.User;

import java.util.Date;
import java.util.List;

public interface AuthService {
    String renewToken(String tokenStr) throws Exception;

    String getSubject(String token);

    String createToken(String login, String issuer, List<String> roles);

    boolean validateUser(Long userId, String password) throws InvalidPasswordException;

    String authorizeUser(User user, String pwd) throws InvalidPasswordException;


    void logRecord(Date logDate, String action, String subject, String record);
}
