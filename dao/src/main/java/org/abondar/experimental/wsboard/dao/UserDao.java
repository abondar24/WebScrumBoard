package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.dao.data.ObjectWrapper;
import org.abondar.experimental.wsboard.dao.password.PasswordUtil;
import org.abondar.experimental.wsboard.dao.password.exception.CannotPerformOperationException;
import org.abondar.experimental.wsboard.dao.password.exception.InvalidHashException;
import org.abondar.experimental.wsboard.datamodel.User;
import org.abondar.experimental.wsboard.datamodel.UserRole;
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
     * @return Object wrapper with user POJO or Object wrapper with error message
     * @throws CannotPerformOperationException - password hash creation failed
     */
    public ObjectWrapper<User> createUser(String login, String password, String email, String firstName,
                                          String lastName, List<String> roles) throws CannotPerformOperationException {
        ObjectWrapper<User> res = new ObjectWrapper<>();
        var usr = mapper.getUserByLogin(login);

        if (usr != null) {
            logger.error(ErrorMessageUtil.USER_EXISTS);
            res.setMessage(ErrorMessageUtil.USER_EXISTS);

            return res;
        }

        var pwdHash = PasswordUtil.createHash(password);

        var userRoles = roles.stream().filter(this::containsRole).collect(Collectors.joining(";"));
        if (userRoles.isBlank()) {
            logger.error(ErrorMessageUtil.USER_NO_ROLES);
            res.setMessage(ErrorMessageUtil.USER_NO_ROLES);

            return res;
        }

        usr = new User(login, email, firstName, lastName, pwdHash, userRoles);
        mapper.insertUser(usr);

        logger.info("User successfully created with id: " + usr.getId());
        res.setObject(usr);

        return res;
    }

    /**
     * Update user login
     * @param login - user login
     * @param userId - user id
     *
     * @return Object wrapper with updated user POJO or Object wrapper with error message
     */
    public ObjectWrapper<User> updateLogin(String login, long userId) {
        ObjectWrapper<User> res = new ObjectWrapper<>();
        if (login.isBlank()) {
            logger.error(ErrorMessageUtil.USER_EMTPY_LOGIN);
            res.setMessage(ErrorMessageUtil.USER_EMTPY_LOGIN);
            res.setObject(null);
            return res;
        }

        var usr = mapper.getUserByLogin(login);
        if (usr != null) {
            logger.error(ErrorMessageUtil.USER_EXISTS);
            res.setMessage(ErrorMessageUtil.USER_EXISTS);

            return res;
        }

        usr = mapper.getUserById(userId);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXISTS + " with id: " + userId);
            res.setMessage(ErrorMessageUtil.USER_NOT_EXISTS);

            return res;
        }

        usr.setLogin(login);
        mapper.updateUser(usr);

        logger.info("User login updated for user: " + usr.getId());
        res.setObject(usr);

        return res;
    }

    /**
     * Update user password
     *
     * @param oldPassword - user password to be changed
     * @param newPassword - user new password
     * @param userId      - user id
     * @return Object wrapper with updated user POJO or Object wrapper with error message
     * @throws CannotPerformOperationException - password hash creation failed
     * @throws InvalidHashException            - old user password doesn't match the one in db
     */
    public ObjectWrapper<User> updatePassword(String oldPassword, String newPassword, long userId)
            throws CannotPerformOperationException, InvalidHashException {
        ObjectWrapper<User> res = new ObjectWrapper<>();

        var usr = mapper.getUserById(userId);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXISTS + " with id: " + userId);
            res.setMessage(ErrorMessageUtil.USER_NOT_EXISTS);
            return res;
        }

        if (!PasswordUtil.verifyPassword(oldPassword, usr.getPassword())) {
            logger.error(ErrorMessageUtil.USER_UNAUTHORIZED);
            res.setMessage(ErrorMessageUtil.USER_UNAUTHORIZED);
            return res;
        }
        usr.setPassword(PasswordUtil.createHash(newPassword));
        mapper.updateUser(usr);

        logger.info("Password updated for user: " + usr.getId());

        res.setObject(usr);
        return res;
    }

    /**
     * Update user data
     * @param id - user id
     * @param firstName - user first name
     * @param lastName - user last name
     * @param email - user email
     * @param roles - list of user roles
     * @param avatar - user avatar
     *
     * @return Object wrapper with updated user POJO or Object wrapper with error message
     */
    public ObjectWrapper<User> updateUser(Long id, String firstName,
                                          String lastName, String email,
                                          List<String> roles, byte[] avatar) {
        ObjectWrapper<User> res = new ObjectWrapper<>();

        var usr = mapper.getUserById(id);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXISTS + " with id: " + id);
            res.setMessage(ErrorMessageUtil.USER_NOT_EXISTS);

            return res;
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
            res.setMessage(ErrorMessageUtil.USER_AVATAR_EMPTY);

            return res;
        }

        res.setObject(usr);
        return res;
    }

    /**
     * Mark user as deleted and set all its data as 'deleted'
     * @param id - user id
     *
     * @return Object wrapper with updated user POJO or Object wrapper with error message
     */
    public ObjectWrapper<User> deleteUser(Long id) {
        ObjectWrapper<User> res = new ObjectWrapper<>();

        var usr = mapper.getUserById(id);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXISTS + " with id: " + id);
            res.setMessage(ErrorMessageUtil.USER_NOT_EXISTS);

            return res;
        }

        var contributor = mapper.getContributorByUserId(id);
        if (contributor != null) {
            if (contributor.isOwner()) {
                logger.error(ErrorMessageUtil.USER_IS_PROJECT_OWNER);
                res.setMessage(ErrorMessageUtil.USER_IS_PROJECT_OWNER);

                return res;
            }

            usr.setDeleted();

            var ctr = contributorDao.updateContributor(contributor.getId(), contributor.isOwner(), false);

            if (ctr.getMessage() != null) {
                logger.error(ctr.getMessage());
                res.setMessage(ctr.getMessage());

                return res;
            }
        }


        mapper.insertUser(usr);
        logger.info("User with id: " + usr.getId() + " marked as deleted");

        res.setObject(usr);
        return res;
    }

    /**
     * Perform user log in
     *
     * @param login    - user login
     * @param password - user password
     * @return error message or empty string
     * @throws CannotPerformOperationException - password hash decoding failed
     * @throws InvalidHashException            - password doesn't match the one in db
     */
    public String loginUser(String login, String password)
            throws CannotPerformOperationException, InvalidHashException {

        var usr = mapper.getUserByLogin(login);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXISTS + "with login: " + login);

            return ErrorMessageUtil.USER_NOT_EXISTS;
        }

        if (!PasswordUtil.verifyPassword(password, usr.getPassword())) {
            logger.error(ErrorMessageUtil.USER_UNAUTHORIZED);

            return ErrorMessageUtil.USER_UNAUTHORIZED;
        }

        return "";
    }

    /**
     * Perform user log out
     * @param id - user login
     *
     * @return error message or empty string
     */
    public String logoutUser(long id) {

        var usr = mapper.getUserById(id);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXISTS + " with id: " + id);

            return ErrorMessageUtil.USER_NOT_EXISTS;
        }

        return "";
    }

    /**
     * Check if role exists in the list of available roles
     * @param role - user role
     *
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
