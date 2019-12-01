package org.abondar.experimental.wsboard.dao.data;

/**
 * List of error messages
 *
 * @author a.bondar
 */
public class LogMessageUtil {

    public static final String USER_EXISTS = "User with login already exists";
    public static final String USER_NO_ROLES = "No roles are set";
    public static final String USER_EMTPY_LOGIN = "Got empty login";
    public static final String USER_NOT_EXISTS = "User not exists";
    public static final String USER_UNAUTHORIZED = "Unauthorized action";
    public static final String USER_IS_PROJECT_OWNER = "User is project owner and can't be deleted";
    public static final String PROJECT_EXISTS = "Project with name already exists";
    public static final String PROJECT_NOT_EXISTS = "Project doesn't exist";
    public static final String WRONG_END_DATE = "End date must be after a start date";
    public static final String PROJECT_CANNOT_BE_REACTIVATED = "Project can't be reactivated";
    public static final String PROJECT_HAS_NO_OWNER = "Project has no owner";
    public static final String PARSE_DATE_FAILED = "Date string is incorrect";
    public static final String PROJECT_NOT_ACTIVE = "Current project is not active";
    public static final String CONTRIBUTOR_IS_ALREADY_OWNER = "This contributor is already an owner";
    public static final String CONTRIBUTOR_NOT_EXISTS = "Contributor not found";
    public static final String CONTRIBUTOR_NOT_ACTIVE = "Contributor is not active";
    public static final String CONTRIBUTOR_CANNOT_BE_DEACTIVATED = "Contributor can't be deactivated as is project owner ";
    public static final String TASK_NOT_EXISTS = "Task doesn't exist";
    public static final String TASK_ALREADY_COMPLETED = "Cant't update already completed task.";
    public static final String TASK_ALREADY_CREATED = "Task already created";
    public static final String TASK_WRONG_STATE_AFTER_PAUSE = "Task was paused in different state";
    public static final String TASK_STATE_UNKNOWN = "Task state not exists";
    public static final String TASK_DEV_OPS_NOT_ENABLED = "Task doesn't require DEV_OPS state";
    public static final String TASK_CONTRIBUTOR_UPDATE = "Task contributor must be changed";
    public static final String TASK_MOVE_NOT_AVAILABLE = "Task can't be moved to selected state";
    public static final String SPRINT_EXISTS = "Sprint with such name exists";
    public static final String SPRINT_NOT_EXISTS = "Sprint not found";
    public static final String SPRINT_ACTIVE_EXISTS = "Project already has an active sprint";
    public static final String BLANK_DATA = "Data for creation or updating is empty";
    public static final String VERIFICATION_FAILED = "Password verification failed!";
    public static final String NULL_PASS = "Supplied password is null!";
    public static final String LOG_FORMAT = "%s with id: %d";
    public static final String LOG_COUNT_FORMAT = "%s with id %d:  %d";
    public static final String CODE_NOT_EXISTS = "Security code does not exist";
    public static final String CODE_NOT_MATCHES = "Codes don't match";
    public static final String WRONG_PWD = "Password is wrong";
    public static final String CONTRIBUTOR_EXISTS_LOG = "Contributor for selected project %d exists for user %d";
    public static final String CONTRIBUTOR_EXISTS_EX = "Contributor for selected project exists for this user";

    private LogMessageUtil() {
    }


}
