package org.abondar.experimental.wsboard.datamodel;

public class Contributor {

    private long id;
    private long userId;
    private long projectId;
    private boolean isOwner;

    public Contributor(){}

    public Contributor(long userId, long projectId, boolean isOwner) {
        this.userId = userId;
        this.projectId = projectId;
        this.isOwner = isOwner;
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

    @Override
    public String toString() {
        return "Contributor{" +
                "id=" + id +
                ", userId=" + userId +
                ", projectId=" + projectId +
                ", isOwner=" + isOwner +
                '}';
    }
}
