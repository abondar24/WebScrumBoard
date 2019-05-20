package org.abondar.experimental.wsboard.datamodel.task;

/**
 * Set of available task states
 *
 * @author a.bondar
 */
public enum TaskState {

    CREATED,
    IN_DEVELOPMENT,
    PAUSED,
    IN_CODE_REVIEW,
    IN_TEST,
    IN_DEPLOYMENT,
    COMPLETED
}
