package org.abondar.experimental.wsboard.server.datamodel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * POJO for Contributor data model
 * @author a.bondar
 */
@ApiModel(value = "Contributor", description = "Project contributor")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Contributor implements Serializable {

    private static final long serialVersionUID = -345433L;

    @ApiModelProperty(value = "Contributor id")
    private long id;

    @ApiModelProperty(value = "Contributor user id")
    private long userId;

    @ApiModelProperty(value = "Contributor project id")
    private long projectId;

    @ApiModelProperty(value = "Is contributor owner or not")
    private boolean isOwner;

    @ApiModelProperty(value = "Is contributor active or not")
    private boolean isActive;


    public Contributor(long userId, long projectId, boolean isOwner) {
        this.userId = userId;
        this.projectId = projectId;
        this.isOwner = isOwner;
        this.isActive = true;
    }


}
