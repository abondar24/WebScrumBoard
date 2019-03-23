package org.abondar.experimental.wsboard.base.data;

public class ErrorMessageUtil {

    public static final String USER_EXISTS = "User with login already exists";
    public static final String NO_ROLES = "No roles are set";
    public static final String EMTPY_LOGIN = "Got empty login";
    public static final String USER_NOT_EXIST = "User not exists";
    public static final String UNAUTHORIZED = "Unauthorized action";
    public static final String USER_AVATAR_EMPTY = "Image data not set";
    public static final String PROJECT_EXISTS = "Project with name already exists";
    public static final String PROJECT_NOT_EXISTS = "Project doesn't exist";
    public static final String WRONG_END_DATE = "End date must be after a start date";
    public static final String PROJECT_NOT_ACTIVE = "Current project is not active";
    public static final String PROJECT_HAS_OWNER = "Project already has an owner";
    public static final String CONTRIBUTOR_NOT_EXISTS = "Contributor not found";
    public static final String PROJECT_HAS_NO_OWNER = "Project has no owner";
    public static final String USER_IS_PROJECT_OWNER = "User is project owner and can't be deleted";

    private ErrorMessageUtil(){}


}
