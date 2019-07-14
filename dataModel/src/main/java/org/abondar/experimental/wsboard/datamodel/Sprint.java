package org.abondar.experimental.wsboard.datamodel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * POJO for Sprint data model
 *
 * @author a.bondar
 */
@ApiModel(value = "Sprint", description = "Project sprint")
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

    //TODO: link sprint to a project


    public Sprint(){}

    public Sprint(String name, Date startDate, Date endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Sprint{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
