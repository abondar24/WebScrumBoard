package org.abondar.experimental.wsboard.server.datamodel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * POJO for Sprint data model
 *
 * @author a.bondar
 */
@ApiModel(value = "Sprint", description = "Project sprint")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Sprint implements Serializable {

    private static final long serialVersionUID = -125433L;

    @ApiModelProperty(value = "Sprint id")
    private long id;

    @ApiModelProperty(value = "Sprint name")
    private String name;

    @ApiModelProperty(value = "Sprint start date")
    private Date startDate;

    @ApiModelProperty(value = "Sprint end date")
    private Date endDate;

    @ApiModelProperty(value = "Sprint project id")
    private long projectId;

    @ApiModelProperty(value = "Is sprint a current one ?")
    private boolean isCurrent;



    public Sprint(String name, Date startDate, Date endDate, long projectId) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectId = projectId;
        this.isCurrent = false;
    }


}
