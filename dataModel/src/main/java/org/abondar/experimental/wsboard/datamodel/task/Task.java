package org.abondar.experimental.wsboard.datamodel.task;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * POJO for Task data model
 *
 * @author a.bondar
 */
@Schema(name = "Task", description = "Project task")
public class Task {

    @Schema(description = "Task id")
    private long id;

    @Schema(description = "Task contributor id")
    private long contributorId;

    @Schema(description = "Task current state")
    private TaskState taskState;

    @Schema(description = "Task previous state")
    private TaskState prevState;

    @Schema(description = "Task story points")
    private int storyPoints;

    @Schema(description = "Task start date")
    private Date startDate;

    @Schema(description = "Task end date")
    private Date endDate;

    @Schema(description = "Task sprint id")
    private long sprintId;

    @Schema(description = "Does't task have dev ops state or not?")
    private boolean devOpsEnabled;


    public Task(){}

    public Task(long contributorId, Date startDate, boolean devOpsEnabled) {
        this.contributorId = contributorId;
        this.startDate = startDate;
        this.devOpsEnabled = devOpsEnabled;
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

    public long getSprintId() {
        return sprintId;
    }

    public void setSprintId(long sprintId) {
        this.sprintId = sprintId;
    }

    public TaskState getPrevState() {
        return prevState;
    }

    public void setPrevState(TaskState prevState) {
        this.prevState = prevState;
    }


    public boolean isDevOpsEnabled() {
        return devOpsEnabled;
    }

    public void setDevOpsEnabled(boolean devOpsEnabled) {
        this.devOpsEnabled = devOpsEnabled;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", contributorId=" + contributorId +
                ", taskState=" + taskState +
                ", prevState=" + prevState +
                ", storyPoints=" + storyPoints +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", sprintId=" + sprintId +
                '}';
    }
}
