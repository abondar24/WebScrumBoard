package org.abondar.experimental.wsboard.server.service;


import org.abondar.experimental.wsboard.server.exception.DataExistenceException;

import java.util.List;

/**
 * Authorization service
 *
 * @author a.bondar
 */
public interface AuthService {

    String createToken(String login, String issuer, List<String> roles);

    boolean validateUser(String login, String password) throws DataExistenceException;

    String authorizeUser(String login, String pwd) throws DataExistenceException, DataExistenceException;

}
