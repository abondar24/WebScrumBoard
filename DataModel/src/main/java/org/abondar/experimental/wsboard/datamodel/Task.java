package org.abondar.experimental.wsboard.datamodel;

import java.util.Date;

public class Task {

    private long id;
    private long userId;
    private TaskState taskState;
    private int storyPoints;
    private Date startDate;
    private Date endDate;


    public Task(){}

    public Task(long userId, TaskState taskState, int storyPoints, Date startDate, Date endDate) {
        this.userId = userId;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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
                ", userId=" + userId +
                ", taskState=" + taskState +
                ", storyPoints=" + storyPoints +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
