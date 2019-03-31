package org.abondar.experimental.wsboard.base.data;

public class ErrorMessageUtil {

    public static final String USER_EXISTS = "User with login already exists";
    public static final String USER_NO_ROLES = "No roles are set";
    public static final String USER_EMTPY_LOGIN = "Got empty login";
    public static final String USER_NOT_EXIST = "User not exists";
    public static final String USER_UNAUTHORIZED = "Unauthorized action";
    public static final String USER_AVATAR_EMPTY = "Image data not set";
    public static final String PROJECT_EXISTS = "Project with name already exists";
    public static final String PROJECT_NOT_EXISTS = "Project doesn't exist";
    public static final String PROJECT_WRONG_END_DATE = "End date must be after a start date";
    public static final String PROJECT_NOT_ACTIVE = "Current project is not active";
    public static final String PROJECT_HAS_OWNER = "Project already has an owner";
    public static final String CONTRIBUTOR_NOT_EXISTS = "Contributor not found";
    public static final String PROJECT_HAS_NO_OWNER = "Project has no owner";
    public static final String USER_IS_PROJECT_OWNER = "User is project owner and can't be deleted";
    public static final String TASK_START_DATE_NOT_SET = "Task start date is null";
    public static final String TASK_NOT_EXISTS = "Task doesn't exist";
    public static final String TASK_STORY_POINTS_NOT_SET = "Story points not set";
    public static final String TASK_ALREADY_COMPLETED = "Cant't update already completed task.";
    public static final String TASK_ALREADY_CREATED = "Task already created";
    public static final String TASK_WRONG_STATE_AFTER_PAUSE = "Task was paused in different state";
    public static final String TASK_TYPE_MISMATCH = "Task type doesn't match contributor role";
    public static final String TASK_TYPE_UNKNOWN = "Task type is unknown";

    private ErrorMessageUtil(){}


}
