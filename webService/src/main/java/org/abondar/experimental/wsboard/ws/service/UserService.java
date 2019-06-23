package org.abondar.experimental.wsboard.ws.service;

import javax.ws.rs.core.Response;

/**
 * User CRUD and login web service
 */
public interface UserService extends RestService {

    Response createUser(String login, String email, String firstName, String lastName,
                        String password, String roles);

    Response updateUser(long id, String firstName, String lastName, String email,
                        String roles);

    Response updateAvatar(long id, byte[] avatar);


    Response updateLogin(String login, long id);

    Response updatePassword(String oldPassword, String newPassword, long id);

    Response deleteUser(long id);

    Response loginUser(String login, String password);

    Response logoutUser(long id);


}
