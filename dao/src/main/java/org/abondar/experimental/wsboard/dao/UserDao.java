package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.CannotPerformOperationException;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.dao.exception.InvalidHashException;
import org.abondar.experimental.wsboard.dao.exception.InvalidPasswordException;
import org.abondar.experimental.wsboard.dao.password.PasswordUtil;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data access object for user
 *
 * @author a.bondar
 */
public class UserDao extends BaseDao {


    private static Logger logger = LoggerFactory.getLogger(UserDao.class);

    private ContributorDao contributorDao;


    public UserDao(DataMapper mapper, BaseDao contributorDao) {
        super(mapper);
        this.contributorDao = (ContributorDao) contributorDao;
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
                           String lastName, List<String> roles)
            throws CannotPerformOperationException, DataCreationException, DataExistenceException {
        var usr = mapper.getUserByLogin(login);

        if (usr != null) {
            logger.error(ErrorMessageUtil.USER_EXISTS);
            throw new DataExistenceException(ErrorMessageUtil.USER_EXISTS);
        }

        if (login == null || login.isBlank()) {
            throw new DataCreationException(ErrorMessageUtil.BLANK_DATA);
        }

        if (password == null || password.isBlank()) {
            throw new DataCreationException(ErrorMessageUtil.BLANK_DATA);
        }

        if (email == null || email.isBlank()) {
            throw new DataCreationException(ErrorMessageUtil.BLANK_DATA);
        }

        if (firstName == null || firstName.isBlank()) {
            throw new DataCreationException(ErrorMessageUtil.BLANK_DATA);
        }

        if (lastName == null || lastName.isBlank()) {
            throw new DataCreationException(ErrorMessageUtil.BLANK_DATA);
        }

        if (roles == null || roles.isEmpty()) {
            throw new DataCreationException(ErrorMessageUtil.BLANK_DATA);
        }

        var pwdHash = PasswordUtil.createHash(password);

        var userRoles = roles.stream().filter(this::containsRole).collect(Collectors.joining(";"));

        usr = new User(login, email, firstName, lastName, pwdHash, userRoles);
        mapper.insertUser(usr);

        logger.info("User successfully created with id: " + usr.getId());

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
            logger.error(ErrorMessageUtil.USER_EMTPY_LOGIN);
            throw new DataCreationException(ErrorMessageUtil.USER_EMTPY_LOGIN);
        }

        var usr = mapper.getUserByLogin(login);
        if (usr != null) {
            logger.error(ErrorMessageUtil.USER_EXISTS);
            throw new DataExistenceException(ErrorMessageUtil.USER_EXISTS);

        }

        usr = mapper.getUserById(userId);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXISTS + " with id: " + userId);
            throw new DataExistenceException(ErrorMessageUtil.USER_NOT_EXISTS);
        }

        usr.setLogin(login);
        mapper.updateUser(usr);

        logger.info("User login updated for user: " + usr.getId());

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
     * @throws InvalidPasswordException        - old password not correct
     */
    public User updatePassword(String oldPassword, String newPassword, long userId)
            throws CannotPerformOperationException, InvalidHashException, DataExistenceException {

        var usr = mapper.getUserById(userId);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXISTS + " with id: " + userId);
            throw new DataExistenceException(ErrorMessageUtil.USER_NOT_EXISTS);
        }

        if (!PasswordUtil.verifyPassword(oldPassword, usr.getPassword())) {
            logger.error(ErrorMessageUtil.USER_UNAUTHORIZED);
            throw new InvalidPasswordException(ErrorMessageUtil.USER_UNAUTHORIZED);
        }
        usr.setPassword(PasswordUtil.createHash(newPassword));
        mapper.updateUser(usr);

        logger.info("Password updated for user: " + usr.getId());

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
     */
    public User updateUser(Long id, String firstName,
                           String lastName, String email,
                           List<String> roles, byte[] avatar)
            throws DataExistenceException, DataCreationException {

        var usr = mapper.getUserById(id);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXISTS + " with id: " + id);
            throw new DataExistenceException(ErrorMessageUtil.USER_NOT_EXISTS);
        }

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
            var userRoles = roles.stream().filter(this::containsRole).collect(Collectors.joining(";"));
            usr.setRoles(userRoles);
        }

        if (avatar != null && avatar.length == 0) {
            logger.error(ErrorMessageUtil.USER_AVATAR_EMPTY);
            throw new DataCreationException(ErrorMessageUtil.USER_AVATAR_EMPTY);
        }

        mapper.updateUser(usr);

        return usr;
    }

    /**
     * Mark user as deleted and set all its data as 'deleted'
     *
     * @param id - user id
     * @return user POJO
     */
    public User deleteUser(long id) throws DataExistenceException, DataCreationException {
        var usr = mapper.getUserById(id);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXISTS + " with id: " + id);
            throw new DataExistenceException(ErrorMessageUtil.USER_NOT_EXISTS);
        }

        var contributor = mapper.getContributorByUserId(id);
        if (contributor != null) {
            if (contributor.isOwner()) {
                logger.error(ErrorMessageUtil.USER_IS_PROJECT_OWNER);
                throw new DataCreationException(ErrorMessageUtil.USER_IS_PROJECT_OWNER);

            }

            usr.setDeleted();

            contributorDao.updateContributor(contributor.getId(), contributor.isOwner(), false);

        }


        mapper.insertUser(usr);
        logger.info("User with id: " + usr.getId() + " marked as deleted");

        return usr;
    }

    /**
     * Perform user log in
     *
     * @param login    - user login
     * @param password - user password
     * @throws CannotPerformOperationException - password hash decoding failed
     * @throws InvalidHashException            - password doesn't match the one in db
     */
    public void loginUser(String login, String password)
            throws CannotPerformOperationException, InvalidHashException, DataExistenceException {

        var usr = mapper.getUserByLogin(login);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXISTS + "with login: " + login);
            throw new DataExistenceException(ErrorMessageUtil.USER_NOT_EXISTS);
        }

        if (!PasswordUtil.verifyPassword(password, usr.getPassword())) {
            logger.error(ErrorMessageUtil.USER_UNAUTHORIZED);
            throw new InvalidPasswordException(ErrorMessageUtil.USER_UNAUTHORIZED);
        }

    }

    /**
     * Perform user log out
     *
     * @param id - user login
     */
    public void logoutUser(long id) throws DataExistenceException {

        var usr = mapper.getUserById(id);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXISTS + " with id: " + id);

            throw new DataExistenceException(ErrorMessageUtil.USER_NOT_EXISTS);
        }

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
