package org.abondar.experimental.wsboard.datamodel;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * POJO for Sprint data model
 *
 * @author a.bondar
 */
@Schema(name = "Sprint", description = "Project sprint")
public class Sprint {

    @Schema(description = "Sprint id")
    private long id;

    @Schema(description = "Sprint name")
    private String name;

    @Schema(description = "Sprint start date")
    private Date startDate;

    @Schema(description = "Sprint end date")
    private Date endDate;

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
