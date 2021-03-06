package org.abondar.experimental.wsboard.server.dao;

import org.abondar.experimental.wsboard.server.datamodel.Contributor;
import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.abondar.experimental.wsboard.server.datamodel.user.UserRole;
import org.abondar.experimental.wsboard.server.exception.CannotPerformOperationException;
import org.abondar.experimental.wsboard.server.exception.DataCreationException;
import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.abondar.experimental.wsboard.server.exception.InvalidHashException;
import org.abondar.experimental.wsboard.server.mapper.ContributorMapper;
import org.abondar.experimental.wsboard.server.mapper.UserMapper;
import org.abondar.experimental.wsboard.server.util.LogMessageUtil;
import org.abondar.experimental.wsboard.server.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;


/**
 * Data access object for user
 *
 * @author a.bondar
 */
@Component
public class UserDao {

    private final PlatformTransactionManager transactionManager;

    private final UserMapper userMapper;

    private final ContributorMapper contributorMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    @Autowired
    public UserDao(UserMapper userMapper, ContributorMapper contributorMapper, PlatformTransactionManager transactionManager) {
        this.userMapper = userMapper;
        this.contributorMapper = contributorMapper;
        this.transactionManager = transactionManager;
    }

    /**
     * Create a new user
     *
     * @param login     - user login
     * @param password  - user password
     * @param email     - user email
     * @param firstName - user first name
     * @param lastName  - user last name
     * @param roles     - list of user roles
     * @return user POJO
     * @throws CannotPerformOperationException - password hash creation failed
     * @throws DataCreationException           - user creation failed
     * @throws DataExistenceException          - user already exists
     */
    public User createUser(String login, String password, String email, String firstName,
                           String lastName, String roles)
            throws CannotPerformOperationException, DataCreationException, DataExistenceException {
        var usr = userMapper.getUserByLogin(login);

        if (usr != null) {
            logger.error(LogMessageUtil.USER_EXISTS);
            throw new DataExistenceException(LogMessageUtil.USER_EXISTS);
        }

        if (login == null || login.isBlank()) {
            throw new DataCreationException(LogMessageUtil.BLANK_DATA);
        }

        if (password == null || password.isBlank()) {
            throw new DataCreationException(LogMessageUtil.BLANK_DATA);
        }

        if (email == null || email.isBlank()) {
            throw new DataCreationException(LogMessageUtil.BLANK_DATA);
        }

        if (firstName == null || firstName.isBlank()) {
            throw new DataCreationException(LogMessageUtil.BLANK_DATA);
        }

        if (lastName == null || lastName.isBlank()) {
            throw new DataCreationException(LogMessageUtil.BLANK_DATA);
        }

        if (roles == null || roles.isBlank()) {
            throw new DataCreationException(LogMessageUtil.BLANK_DATA);
        }

        var pwdHash = PasswordUtil.createHash(password);

        usr = new User(login, email, firstName, lastName, pwdHash, checkRoles(roles));
        userMapper.insertUser(usr);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "User successfully created ", usr.getId());
        logger.info(msg);

        return usr;
    }

    /**
     * Update user login
     *
     * @param login  - user login
     * @param userId - user id
     * @return user POJO
     * @throws DataCreationException  - login update failed
     * @throws DataExistenceException - user already exists with such login, user not found
     */
    public User updateLogin(String login, long userId) throws DataCreationException, DataExistenceException {
        if (login.isBlank()) {
            logger.error(LogMessageUtil.USER_EMTPY_LOGIN);
            throw new DataCreationException(LogMessageUtil.USER_EMTPY_LOGIN);
        }

        var usr = userMapper.getUserByLogin(login);
        if (usr != null) {
            logger.error(LogMessageUtil.USER_EXISTS);
            throw new DataExistenceException(LogMessageUtil.USER_EXISTS);

        }

        usr = findUserById(userId);

        usr.setLogin(login);
        userMapper.updateUser(usr);

        var msg = String.format("%s %d", "User login updated for user: ", usr.getId());
        logger.info(msg);

        return usr;
    }

    /**
     * Update user password
     *
     * @param oldPassword - user password to be changed
     * @param newPassword - user new password
     * @param userId      - user id
     * @return user POJO
     * @throws CannotPerformOperationException - password hash creation failed
     * @throws InvalidHashException            - old user password doesn't match the one in db
     * @throws DataExistenceException          - user not exists
     */
    public User updatePassword(String oldPassword, String newPassword, long userId)
            throws CannotPerformOperationException, InvalidHashException, DataExistenceException {

        var usr = findUserById(userId);

        if (!PasswordUtil.verifyPassword(oldPassword, usr.getPassword())) {
            throw new InvalidHashException(LogMessageUtil.WRONG_PWD);
        }
        usr.setPassword(PasswordUtil.createHash(newPassword));
        userMapper.updateUser(usr);

        var msg = String.format("%s %d", "Password updated for user: ", usr.getId());
        logger.info(msg);

        return usr;
    }

    /**
     * Update user data
     *
     * @param id        - user id
     * @param firstName - user first name
     * @param lastName  - user last name
     * @param email     - user email
     * @param roles     - list of user roles
     * @param avatar    - user avatar
     * @return user POJO
     * @throws DataExistenceException - user not found
     * @throws DataCreationException  - user can't be updated
     */
    public User updateUser(Long id, String firstName,
                           String lastName, String email,
                           String roles, String avatar)
            throws DataExistenceException, DataCreationException {

        var usr = findUserById(id);

        if (email != null && !email.isBlank()) {
            usr.setEmail(email);
        }

        if (firstName != null && !firstName.isBlank()) {
            usr.setFirstName(firstName);
        }

        if (lastName != null && !lastName.isBlank()) {
            usr.setLastName(lastName);
        }

        if (roles != null && !roles.isEmpty()) {
            usr.setRoles(checkRoles(roles));
        }

        if (avatar != null) {
            usr.setAvatar(avatar);
        }

        userMapper.updateUser(usr);

        return usr;
    }

    public void resetPassword(long id) throws DataExistenceException, CannotPerformOperationException {
        var usr = userMapper.getUserById(id);
        if (usr == null) {
            var msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.USER_NOT_EXISTS, id);
            logger.error(msg);

            throw new DataExistenceException(LogMessageUtil.USER_NOT_EXISTS);
        }

        usr.setPassword(PasswordUtil.createHash("reset"));
        userMapper.updateUser(usr);
        var msg = String.format("Password reset for " + LogMessageUtil.LOG_FORMAT, "user ", usr.getId());
        logger.info(msg);

    }

    /**
     * Mark user as deleted and set all its data as 'deleted'
     *
     * @param id - user id
     * @return user POJO
     * @throws DataExistenceException - user not found
     * @throws DataCreationException  - user can't be updated
     */
    public User deleteUser(long id) throws DataExistenceException, DataCreationException {
        TransactionStatus txStatus =
                transactionManager.getTransaction(new DefaultTransactionDefinition());
        User usr;
        try {
            usr = findUserById(id);
        } catch (DataExistenceException ex) {
            transactionManager.rollback(txStatus);
            throw new DataExistenceException(ex.getMessage());
        }

        var contributors = contributorMapper.getContributorsByUserId(id, -1, 0);
        if (!contributors.isEmpty()) {
            var isOwnerOnce = contributors.stream().anyMatch(Contributor::isOwner);
            if (isOwnerOnce) {
                logger.error(LogMessageUtil.USER_IS_PROJECT_OWNER);
                transactionManager.rollback(txStatus);
                throw new DataCreationException(LogMessageUtil.USER_IS_PROJECT_OWNER);
            }

            contributorMapper.deactivateUserContributors(usr.getId());
        }

        usr.setDeleted();

        userMapper.updateUser(usr);

        var msg = String.format(LogMessageUtil.LOG_FORMAT + "%s", "User ", usr.getId(), " marked as deleted");
        logger.info(msg);
        transactionManager.commit(txStatus);

        return usr;
    }


    /**
     * Find user By Id
     *
     * @param id - user id
     * @return user with id
     * @throws DataExistenceException - user not found
     */
    public User findUserById(long id) throws DataExistenceException {

        var usr = userMapper.getUserById(id);
        if (usr == null) {
            var msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.USER_NOT_EXISTS, id);
            logger.error(msg);

            throw new DataExistenceException(LogMessageUtil.USER_NOT_EXISTS);
        }

        if (usr.getFirstName().equals("deleted")) {
            throw new DataExistenceException(LogMessageUtil.USER_NOT_EXISTS);
        }

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "User ", usr.getId());
        logger.info(msg);

        return usr;
    }

    /**
     * Get users by ids
     *
     * @param ids - user ids
     * @return - list of users
     */
    public List<User> findUsersByIds(List<Long> ids) {
        return userMapper.getUsersByIds(ids);
    }

    /**
     * Find user By Login
     *
     * @param login - user login
     * @return user with login
     * @throws DataExistenceException - user not found
     */
    public User findUserByLogin(String login) throws DataExistenceException {

        if (login.equals("deleted")) {
            throw new DataExistenceException(LogMessageUtil.USER_NOT_EXISTS);
        }

        var usr = userMapper.getUserByLogin(login);
        if (usr == null) {
            logger.error(LogMessageUtil.USER_NOT_EXISTS);

            throw new DataExistenceException(LogMessageUtil.USER_NOT_EXISTS);
        }

        logger.info("User found with required login");
        return usr;
    }


    /**
     * Checks if user roles are correct
     *
     * @param roles - ; separated list of roles
     * @return list of roles with ;
     * @throws DataCreationException - user roles empty or or don't have delimiter
     */
    private String checkRoles(String roles) throws DataCreationException {

        String[] rolesArr = roles.split(";");
        if (rolesArr.length == 0) {
            throw new DataCreationException(LogMessageUtil.USER_NO_ROLES);
        }

        var userRoles = new StringBuilder();
        for (String role : rolesArr) {

            if (containsRole(role.toUpperCase())) {
                userRoles.append(role);
                userRoles.append(";");
            }

        }

        if (userRoles.toString().isBlank()) {
            throw new DataCreationException(LogMessageUtil.USER_NO_ROLES);
        }

        return userRoles.toString();

    }

    /**
     * Check if role exists in the list of available roles
     *
     * @param role - user role
     * @return true if exists, or false doesn't
     */
    private boolean containsRole(String role) {
        for (UserRole r : UserRole.values()) {
            if (r.name().equals(role)) {
                return true;
            }
        }

        return false;
    }


}
