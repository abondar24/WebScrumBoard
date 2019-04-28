package org.abondar.experimental.wsboard.dao.password.exception;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException(String message, Throwable ex) {
        super(message, ex);
    }
}
