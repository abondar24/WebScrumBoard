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

    private long userId;

    public SecurityCode() {
    }

    public SecurityCode(long code, long userId) {
        this.code = code;
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
                ", userId=" + userId +
                '}';
    }
}
