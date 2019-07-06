package org.abondar.experimental.wsboard.datamodel.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Set of available user roles
 *
 * @author a.bondar
 */
@ApiModel(value = "UserRole", description = "User role")
public enum UserRole {

    @ApiModelProperty(value = "DEVELOPER")
    DEVELOPER,

    @ApiModelProperty(value = "QA")
    QA,

    @ApiModelProperty(value = "DEV_OPS")
    DEV_OPS
}
