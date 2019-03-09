package org.abondar.experimental.wsboard.datamodel;

import java.util.Date;

public class Task {

    private long id;
    private long contributorId;
    private TaskState taskState;
    private int storyPoints;
    private Date startDate;
    private Date endDate;


    public Task(){}

    public Task(long contributorId, TaskState taskState, int storyPoints, Date startDate, Date endDate) {
        this.contributorId = contributorId;
        this.taskState = taskState;
        this.storyPoints = storyPoints;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getContributorId() {
        return contributorId;
    }

    public void setContributorId(long contributorId) {
        this.contributorId = contributorId;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    public int getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(int storyPoints) {
        this.storyPoints = storyPoints;
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
        return "Task{" +
                "id=" + id +
                ", contributorId=" + contributorId +
                ", taskState=" + taskState +
                ", storyPoints=" + storyPoints +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}