package org.abondar.experimental.wsboard.datamodel.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * POJO for Task data model
 *
 * @author a.bondar
 */
@ApiModel(value = "Task", description = "Project task")
public class Task implements Serializable {

    private static final long serialVersionUID = -1276893L;

    @ApiModelProperty(value = "Task id")
    private long id;

    @ApiModelProperty(value = "Task contributor id")
    private long contributorId;

    @ApiModelProperty(value = "Task current state")
    private TaskState taskState;

    @ApiModelProperty(value = "Task previous state")
    private TaskState prevState;

    @ApiModelProperty(value = "Task story points")
    private int storyPoints;

    @ApiModelProperty(value = "Task start date in format dd/MM/yyyy")
    private Date startDate;

    @ApiModelProperty(value = "Task end in date in format dd/MM/yyyy")
    private Date endDate;

    @ApiModelProperty(value = "Task sprint id")
    private long sprintId;

    @ApiModelProperty(value = "Does't task have dev ops state or not?")
    private boolean devOpsEnabled;

    @ApiModelProperty(value = "Task name")
    private String taskName;

    @ApiModelProperty(value = "Task description")
    private String taskDescription;

    public Task(){}

    public Task(long contributorId, Date startDate, boolean devOpsEnabled, String taskName, String taskDescription) {
        this.contributorId = contributorId;
        this.startDate = startDate;
        this.devOpsEnabled = devOpsEnabled;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
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
                ", devOpsEnabled=" + devOpsEnabled +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                '}';
    }
}
