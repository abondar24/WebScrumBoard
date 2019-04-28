package org.abondar.experimental.wsboard.dao.data;

public class ObjectWrapper<T> {

    private String message;
    private T object;

    public void setMessage(String message) {
        this.message = message;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Wrapper{" +
                "message='" + message + '\'' +
                ", object=" + object +
                '}';
    }
}
