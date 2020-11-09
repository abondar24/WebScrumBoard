package org.abondar.experimental.wsboard.common.exception;

@SuppressWarnings("serial")
/**
 *
 * Exception thrown if hash is wrong
 * @author a.bondar
 */
public class InvalidHashException extends Exception {

    public InvalidHashException(String message) {
        super(message);
    }

    public InvalidHashException(String message, Throwable source) {
        super(message, source);
    }
}
