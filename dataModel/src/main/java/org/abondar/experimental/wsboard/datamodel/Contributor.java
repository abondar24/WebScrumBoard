package org.abondar.experimental.wsboard.datamodel;

/*
 * POJO for Contributor data model
 * @author a.bondar
 */
public class Contributor {

    private long id;
    private long userId;
    private long projectId;
    private boolean isOwner;
    private boolean isActive;

    public Contributor(){}

    public Contributor(long userId, long projectId, boolean isOwner) {
        this.userId = userId;
        this.projectId = projectId;
        this.isOwner = isOwner;
        this.isActive = true;
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

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Contributor{" +
                "id=" + id +
                ", userId=" + userId +
                ", projectId=" + projectId +
                ", isOwner=" + isOwner +
                ", isActive=" + isActive +
                '}';
    }
}
