package org.abondar.experimental.wsboard.test.dao;

import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.dao.SecurityCodeDao;
import org.abondar.experimental.wsboard.dao.UserDao;
import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = WebScrumBoardApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class SecurityDaoTest {

    @Autowired
    @Qualifier("codeDao")
    private SecurityCodeDao codeDao;

    @Autowired
    @Qualifier("userDao")
    private UserDao userDao;

    @Autowired
    private DataMapper mapper;


    @Test
    public void insertCodeTest() throws Exception {
        var usr = createUser();
        var code = codeDao.insertCode(usr.getId());

        assertTrue(code > 0);

        cleanData();
    }

    @Test
    public void insertCodeUserNotFoundTest() {
        assertThrows(DataExistenceException.class, () -> codeDao.insertCode(7));

    }


    @Test
    public void enterCodeTest() throws Exception {
        var usr = createUser();
        var code = codeDao.insertCode(usr.getId());

        codeDao.enterCode(usr.getId(), code);
        var sc = mapper.getCodeByUserId(usr.getId());
        assertNull(sc);

        cleanData();
    }

    @Test
    public void enterCodeUserNotFoundTest() {
        assertThrows(DataExistenceException.class, () -> codeDao.enterCode(7, 123));
    }

    @Test
    public void enterCodeNotFoundTest() throws Exception {
        var usr = createUser();

        assertThrows(DataExistenceException.class, () -> codeDao.enterCode(usr.getId(), 123));
        cleanData();
    }

    @Test
    public void enterCodeNotMatchesTest() throws Exception {
        var usr = createUser();
        codeDao.insertCode(usr.getId());

        assertThrows(DataExistenceException.class, () -> codeDao.enterCode(usr.getId(), 123));
        cleanData();
    }


    private User createUser() throws Exception {
        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name();

        return userDao.createUser(login, password, email, firstName, lastName, roles);
    }

    private void cleanData() {
        mapper.deleteCodes();
        mapper.deleteUsers();
    }
}
