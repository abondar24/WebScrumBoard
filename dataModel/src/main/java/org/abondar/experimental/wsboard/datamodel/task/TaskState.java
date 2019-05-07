package org.abondar.experimental.wsboard.datamodel.task;

/**
 * Set of available task states
 *
 * @author a.bondar
 */
public enum TaskState {

    Created,
    InDevelopment,
    Paused,
    InCodeReview,
    InTest,
    InDeployment,
    Completed
}
