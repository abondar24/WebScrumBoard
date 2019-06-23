package org.abondar.experimental.wsboard.datamodel;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * POJO for Project data model
 *
 * @author a.bondar
 */
@Schema(name = "Project", description = "Scrum project")
public class Project {

    @Schema(description = "Project id")
    private long id;

    @Schema(description = "Project name")
    private String name;

    @Schema(description = "Project start date")
    private Date startDate;

    @Schema(description = "Project end date")
    private Date endDate;

    @Schema(description = "Project repository")
    private String repository;

    @Schema(description = "Project is project active or not?")
    private boolean isActive;

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

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", repository='" + repository + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
