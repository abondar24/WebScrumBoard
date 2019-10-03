package org.abondar.experimental.wsboard.datamodel.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * POJO for User data model
 *
 * @author a.bondar
 */
@ApiModel(value = "User", description = "Application user")
public class User implements Serializable {

    private static final long serialVersionUID = -456L;

    private static final String DELETED_DATA = "deleted";

    @ApiModelProperty(value = "User id")
    private long id;

    @ApiModelProperty(value = "User login")
    private String login;

    @ApiModelProperty(value = "User email")
    private String email;

    @ApiModelProperty(value = "User first name")
    private String firstName;

    @ApiModelProperty(value = "User last name")
    private String lastName;

    @ApiModelProperty(value = "User password hash")
    private String password;

    @ApiModelProperty(value = "User roles as ; separated list. Can be: DEVELOPER, QA, DEV_OPS")
    private String roles;

    @ApiModelProperty(value = "User avatar")
    private String avatar;

    public User(){}

    public User(String login, String email, String firstName, String lastName, String password, String roles) {
        this.login = login;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.roles = roles;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setDeleted(){
        this.login = DELETED_DATA;
        this.email = DELETED_DATA;
        this.firstName = DELETED_DATA;
        this.lastName = DELETED_DATA;
        this.password = "";
        this.roles = DELETED_DATA;
        this.avatar = "";
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
