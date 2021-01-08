package org.abondar.experimental.wsboard.server.exception;

/**
 * Exception thrown if password is wrong
 *
 * @author a.bondar
 */
public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException(String message, Throwable ex) {
        super(message, ex);
    }
}
