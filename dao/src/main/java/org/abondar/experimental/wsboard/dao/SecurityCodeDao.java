package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.SecurityCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;


public class SecurityCodeDao extends BaseDao {

    private static Logger logger = LoggerFactory.getLogger(SecurityCodeDao.class);

    public SecurityCodeDao(DataMapper mapper) {
        super(mapper);
    }

    public long insertCode(long userId) throws DataExistenceException {
        var usr = mapper.getUserById(userId);
        if (usr == null) {
            logger.error(LogMessageUtil.USER_NOT_EXISTS);

            throw new DataExistenceException(LogMessageUtil.USER_NOT_EXISTS);
        }

        Random rand = new Random();
        var code = rand.nextLong();

        var codeExists = mapper.checkCodeExists(code);
        if (codeExists != null) {
            throw new DataExistenceException(LogMessageUtil.CODE_ALREADY_EXISTS);
        }

        var sc = new SecurityCode(code, userId);
        mapper.insertCode(sc);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "New security code inserted for user", userId);
        logger.info(msg);

        return code;
    }

}
