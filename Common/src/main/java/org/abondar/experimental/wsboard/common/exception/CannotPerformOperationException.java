package org.abondar.experimental.wsboard.common.exception;

@SuppressWarnings("serial")
/**
 * Exception thrown if hash calculation failed
 * @author a.bondar
 */
public class CannotPerformOperationException extends Exception{
    public CannotPerformOperationException(String message) {
        super(message);
    }

    public CannotPerformOperationException(String message, Throwable source) {
        super(message, source);
    }

}
