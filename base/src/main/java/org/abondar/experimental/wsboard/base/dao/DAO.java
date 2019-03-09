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
            res.setObject(null);

            return res;
        }

        var pwdHash = PasswordUtil.createHash(password);

        var userRoles = roles.stream().filter(this::containsRole).collect(Collectors.joining(";"));
        if (userRoles.isBlank()){
            logger.error(ErrorMessageUtil.NO_ROLES);
            res.setMessage(ErrorMessageUtil.NO_ROLES);
            res.setObject(null);

            return res;
        }

        usr = new User(login,email,firstName,lastName,pwdHash,userRoles);
        mapper.insertUpdateUser(usr);

        logger.info("User successfully created with id: " +usr.getId());
        res.setMessage(null);
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
            res.setObject(null);

            return res;
        }

        usr = mapper.getUserById(userId);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXISTS + " with id: "+userId);
            res.setMessage(ErrorMessageUtil.USER_NOT_EXISTS);
            res.setObject(null);

            return res;
        }

        usr.setLogin(login);
        mapper.insertUpdateUser(usr);

        logger.info("User login updated for user: " +usr.getId());
        res.setMessage(null);
        res.setObject(usr);

        return res;
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
