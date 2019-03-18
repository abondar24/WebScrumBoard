package org.abondar.experimental.wsboard.datamodel;

import java.util.Date;

public class Project {

   private long id;
   private String name;
   private Date startDate;
   private Date endDate;
   private String repository;
   private boolean isActive;

   public Project(){

   }

    public Project(String name, Date startDate) {
        this.name = name;
        this.startDate = startDate;
        this.isActive = false;
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
