package org.abondar.experimental.wsboard.datamodel;

/**
 * POJO for security code table
 *
 * @author a.bondar
 */
public class SecurityCode {

    private static final long serialVersionUID = -235433L;

    private long id;

    private long code;

    private boolean isActivated;

    private long userId;

    public SecurityCode() {
    }

    public SecurityCode(long code, long userId) {
        this.code = code;
        this.isActivated = false;
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "SecurityCode{" +
                "id=" + id +
                ", code=" + code +
                ", isActivated=" + isActivated +
                ", userId=" + userId +
                '}';
    }
}
