package org.abondar.experimental.wsboard.datamodel.user;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * POJO for User data model
 *
 * @author a.bondar
 */
@Schema(name = "User", description = "User model")
public class User {

    private static final String DELETED_DATA = "deleted";
    private long id;
    private String login;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String roles;
    private byte[] avatar;

    public User(){}

    public User(String login, String email, String firstName, String lastName, String password, String roles) {
        this.login = login;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.roles = roles;
    }

    @Schema(description = "user login")
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Schema(description = "user email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Schema(description = "user first name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Schema(description = "user last name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Schema(description = "user password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Schema(description = "comma-separated list of roles", allowableValues = {"DEVELOPER", "QA", "DEV_OPS"})
    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    @Schema(description = "user id generated after creation")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Schema(description = "user avatar in byte array")
    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public void setDeleted(){
        this.login = DELETED_DATA;
        this.email = DELETED_DATA;
        this.firstName = DELETED_DATA;
        this.lastName = DELETED_DATA;
        this.password = "";
        this.roles = DELETED_DATA;
        this.avatar = new byte[]{};
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", roles='" + roles + '\'' +
                '}';
    }
}
