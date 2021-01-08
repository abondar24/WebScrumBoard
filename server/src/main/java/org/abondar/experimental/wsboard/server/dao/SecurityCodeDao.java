package org.abondar.experimental.wsboard.server.dao;

import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.abondar.experimental.wsboard.server.mapper.DataMapper;
import org.abondar.experimental.wsboard.server.util.LogMessageUtil;

import org.abondar.experimental.wsboard.server.datamodel.SecurityCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

@Component
public class SecurityCodeDao extends BaseDao {

    private static final Logger logger = LoggerFactory.getLogger(SecurityCodeDao.class);

    @Autowired
    public SecurityCodeDao(DataMapper mapper) {
        super(mapper);
    }

    public long insertCode(long userId) throws DataExistenceException {
        checkUser(userId);

        var foundCode = mapper.getCodeByUserId(userId);
        if (foundCode != null) {
            mapper.deleteCode(foundCode.getId());
        }

        long code;

        code = generateCode();


        var sc = new SecurityCode(code, userId);
        mapper.insertCode(sc);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "New security code inserted for user", userId);
        logger.info(msg);

        return code;
    }

    public void enterCode(long userId, long code) throws DataExistenceException {
        checkUser(userId);

        var foundCode = mapper.getCodeByUserId(userId);
        if (foundCode == null) {
            throw new DataExistenceException(LogMessageUtil.CODE_NOT_EXISTS);
        }

        if (foundCode.getCode() != code) {
            throw new DataExistenceException(LogMessageUtil.CODE_NOT_MATCHES);
        }

        mapper.deleteCode(foundCode.getId());
    }


    private long generateCode() {

        int num = 0;
        try {
            Random rand = SecureRandom.getInstance("SHA1PRNG");
            num = rand.nextInt(9000000) + 1000000;

        } catch (NoSuchAlgorithmException ex) {
            logger.debug(ex.getMessage());
        }

        return num;
    }


    private void checkUser(long userId) throws DataExistenceException {
        var usr = mapper.getUserById(userId);
        if (usr == null) {
            logger.error(LogMessageUtil.USER_NOT_EXISTS);

            throw new DataExistenceException(LogMessageUtil.USER_NOT_EXISTS);
        }
    }

}
