package org.abondar.experimental.wsboard.test.ws;

import org.abondar.experimental.wsboard.datamodel.user.User;

public class UserWrapper {

    private User usr;

    private String token;

    public UserWrapper(User usr, String token) {
        this.usr = usr;
        this.token = token;
    }

    public User getUsr() {
        return usr;
    }

    public String getToken() {
        return token;
    }
}
