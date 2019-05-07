package org.abondar.experimental.wsboard.dao.exception;

/**
 * Exception thrown if data already exists or not found
 *
 * @author a.bondar
 */

public class DataExistenceException extends Exception {
    public DataExistenceException(String message) {
        super(message);
    }
}
