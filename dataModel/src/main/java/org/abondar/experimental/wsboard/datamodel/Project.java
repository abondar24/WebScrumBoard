package org.abondar.experimental.wsboard.datamodel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * POJO for Project data model
 *
 * @author a.bondar
 */
@ApiModel(value = "Project", description = "Scrum project")
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

    public Project(){

    }

    public Project(String name, Date startDate) {
        this.name = name;
        this.startDate = startDate;
        this.isActive = true;
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

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", repository='" + repository + '\'' +
                ", isActive=" + isActive +
                ", description='" + description + '\'' +
                '}';
    }
}
