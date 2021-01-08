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
 * POJO for Project data model
 *
 * @author a.bondar
 */
@ApiModel(value = "Project", description = "Scrum project")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Project implements Serializable {

    private static final long serialVersionUID = -34556L;

    @ApiModelProperty(value = "Project id")
    private long id;

    @ApiModelProperty(value = "Project name")
    private String name;

    @ApiModelProperty(value = "Project start date in format dd/MM/yyyy")
    private Date startDate;

    @ApiModelProperty(value = "Project end date dd/MM/yyyy")
    private Date endDate;

    @ApiModelProperty(value = "Project repository")
    private String repository;

    @ApiModelProperty(value = "Project is project active or not?")
    private boolean isActive;

    @ApiModelProperty(value = "Project description")
    private String description;


    public Project(String name, Date startDate) {
        this.name = name;
        this.startDate = startDate;
        this.isActive = true;
    }


}
