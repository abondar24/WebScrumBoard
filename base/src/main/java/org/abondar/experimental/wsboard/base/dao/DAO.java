package org.abondar.experimental.wsboard.base.dao;

import org.abondar.experimental.wsboard.base.password.PasswordUtil;
import org.abondar.experimental.wsboard.datamodel.User;
import org.abondar.experimental.wsboard.datamodel.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class DAO {

    private static Logger logger = LoggerFactory.getLogger(DAO.class);

    private DataMapper mapper;

    public DAO(DataMapper mapper){
      this.mapper = mapper;
    }


    public ObjectWrapper<User> createUser(String login, String password, String email, String firstName,
                                    String lastName, List<String> roles) throws Exception {
        ObjectWrapper<User> res = new ObjectWrapper<>();
        var usr = mapper.getUserByLogin(login);

        if (usr != null) {
            logger.error(ErrorMessageUtil.USER_EXISTS);
            res.setMessage(ErrorMessageUtil.USER_EXISTS);

            return res;
        }

        var pwdHash = PasswordUtil.createHash(password);

        var userRoles = roles.stream().filter(this::containsRole).collect(Collectors.joining(";"));
        if (userRoles.isBlank()){
            logger.error(ErrorMessageUtil.NO_ROLES);
            res.setMessage(ErrorMessageUtil.NO_ROLES);

            return res;
        }

        usr = new User(login,email,firstName,lastName,pwdHash,userRoles);
        mapper.insertUpdateUser(usr);

        logger.info("User successfully created with id: " +usr.getId());
        res.setObject(usr);

        return res;
    }

    public ObjectWrapper<User> updateLogin(String login,long userId) {
        ObjectWrapper<User> res = new ObjectWrapper<>();
        if (login.isBlank()){
            logger.error(ErrorMessageUtil.EMTPY_LOGIN);
            res.setMessage(ErrorMessageUtil.EMTPY_LOGIN);
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
            logger.error(ErrorMessageUtil.USER_NOT_EXIST + " with id: "+userId);
            res.setMessage(ErrorMessageUtil.USER_NOT_EXIST);

            return res;
        }

        usr.setLogin(login);
        mapper.insertUpdateUser(usr);

        logger.info("User login updated for user: " +usr.getId());
        res.setObject(usr);

        return res;
    }

    public ObjectWrapper<User> updatePassword(String oldPassword,String newPassword,long userId) throws Exception {
        ObjectWrapper<User> res = new ObjectWrapper<>();

        var usr = mapper.getUserById(userId);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXIST + " with id: "+userId);
            res.setMessage(ErrorMessageUtil.USER_NOT_EXIST);
            res.setObject(null);

            return res;
        }

        if (!PasswordUtil.verifyPassword(oldPassword,usr.getPassword())){
            logger.error(ErrorMessageUtil.UNAUTHORIZED);
            res.setMessage(ErrorMessageUtil.UNAUTHORIZED);
            res.setObject(null);

            return res;
        }
        usr.setPassword(PasswordUtil.createHash(newPassword));
        mapper.insertUpdateUser(usr);

        logger.info("Password updated for user: " +usr.getId());

        res.setObject(usr);
        return res;
    }

    public ObjectWrapper<User> updateUser(Long id,String firstName,
                                          String lastName,String email,List<String> roles) {
        ObjectWrapper<User> res = new ObjectWrapper<>();

        var usr = mapper.getUserById(id);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXIST + " with id: "+id);
            res.setMessage(ErrorMessageUtil.USER_NOT_EXIST);

            return res;
        }

        if (email!=null && !email.isBlank()){
            usr.setEmail(email);
        }

        if (firstName!=null && !firstName.isBlank()){
            usr.setFirstName(firstName);
        }

        if (lastName!=null && !lastName.isBlank()){
            usr.setLastName(lastName);
        }

        if (roles!=null && !roles.isEmpty()){
            var userRoles = roles.stream().filter(this::containsRole).collect(Collectors.joining(";"));
            usr.setRoles(userRoles);
        }

        res.setObject(usr);
        return res;
    }


    public ObjectWrapper<User> updateUserAvatar(Long id,byte[] avatar) {
        ObjectWrapper<User> res = new ObjectWrapper<>();

        var usr = mapper.getUserById(id);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXIST + " with id: "+id);
            res.setMessage(ErrorMessageUtil.USER_NOT_EXIST);

            return res;
        }

        if (avatar==null || avatar.length==0){
            logger.error(ErrorMessageUtil.USER_AVATAR_EMPTY);
            res.setMessage(ErrorMessageUtil.USER_AVATAR_EMPTY);

            return res;
        }


        mapper.updateUserAvatar(id,avatar);
        usr = mapper.getUserById(id);

        res.setObject(usr);
        return res;
    }

    public ObjectWrapper<User> deleteUser(Long id){
        ObjectWrapper<User> res = new ObjectWrapper<>();

        var usr = mapper.getUserById(id);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXIST + " with id: "+id);
            res.setMessage(ErrorMessageUtil.USER_NOT_EXIST);

            return res;
        }

        usr.setDeleted();

        mapper.updateUserAvatar(id,usr.getAvatar());
        mapper.insertUpdateUser(usr);

        res.setObject(usr);
        return res;
    }

    public String loginUser(String login,String password) throws Exception {

        var usr = mapper.getUserByLogin(login);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXIST +"with login: "+login);

            return ErrorMessageUtil.USER_NOT_EXIST;
        }

        if (!PasswordUtil.verifyPassword(password,usr.getPassword())){
            logger.error(ErrorMessageUtil.UNAUTHORIZED);

            return ErrorMessageUtil.UNAUTHORIZED;
        }

        return "";
    }

    public String logoutUser(long id) {

        var usr = mapper.getUserById(id);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXIST + " with id: "+id);

            return ErrorMessageUtil.USER_NOT_EXIST;
        }

        return "";
    }


    private boolean containsRole(String role) {
        for (UserRole r : UserRole.values()) {
            if (r.name().equals(role)) {
                return true;
            }
        }

        return false;
    }


}
