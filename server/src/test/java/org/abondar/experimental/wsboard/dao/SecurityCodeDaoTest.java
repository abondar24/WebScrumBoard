package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.server.dao.SecurityCodeDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class SecurityCodeDaoTest extends BaseDaoTest {
    @Autowired
    @Qualifier("codeDao")
    private SecurityCodeDao codeDao;



    @Test
    public void insertCodeTest() throws Exception {
        cleanData();

        var usr = createUser();
        var code = codeDao.insertCode(usr.getId());

        assertTrue(code > 0);

    }

    @Test
    public void insertCodeUserNotFoundTest() {
        cleanData();
        assertThrows(DataExistenceException.class, () -> codeDao.insertCode(7));

    }


    @Test
    public void enterCodeTest() throws Exception {
        cleanData();
        var usr = createUser();
        var code = codeDao.insertCode(usr.getId());

        codeDao.enterCode(usr.getId(), code);
        var sc = mapper.getCodeByUserId(usr.getId());
        assertNull(sc);

    }

    @Test
    public void enterCodeUserNotFoundTest() {
        cleanData();
        assertThrows(DataExistenceException.class, () -> codeDao.enterCode(7, 123));
    }

    @Test
    public void enterCodeNotFoundTest() throws Exception {
        cleanData();

        var usr = createUser();

        assertThrows(DataExistenceException.class, () -> codeDao.enterCode(usr.getId(), 123));

    }

    @Test
    public void enterCodeNotMatchesTest() throws Exception {
        cleanData();
        var usr = createUser();
        codeDao.insertCode(usr.getId());

        assertThrows(DataExistenceException.class, () -> codeDao.enterCode(usr.getId(), 123));
    }




}
