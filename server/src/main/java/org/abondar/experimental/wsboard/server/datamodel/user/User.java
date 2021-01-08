package org.abondar.experimental.wsboard.server.datamodel.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * POJO for User data model
 *
 * @author a.bondar
 */
@ApiModel(value = "User", description = "Application user")
@Getter
@Setter
@ToString
@NoArgsConstructor
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


    public User(String login, String email, String firstName, String lastName, String password, String roles) {
        this.login = login;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.roles = roles;
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


}
