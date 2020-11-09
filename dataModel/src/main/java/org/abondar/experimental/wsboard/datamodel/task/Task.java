package org.abondar.experimental.wsboard.datamodel.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * POJO for Task data model
 *
 * @author a.bondar
 */
@ApiModel(value = "Task", description = "Project task")
@Getter
@Setter
@ToString
@NoArgsConstructor
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

    public Task(long contributorId, Date startDate, boolean devOpsEnabled, String taskName, String taskDescription) {
        this.contributorId = contributorId;
        this.startDate = startDate;
        this.devOpsEnabled = devOpsEnabled;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }


}
