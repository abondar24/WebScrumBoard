package org.abondar.experimental.wsboard.server.dao;


import org.abondar.experimental.wsboard.server.datamodel.SecurityCode;
import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


public class SecurityCodeDaoTest extends DaoTest {

    @InjectMocks
    private SecurityCodeDao codeDao;

    @Test
    public void insertCodeTest() throws Exception {
        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        when(codeMapper.getCodeByUserId(anyLong())).thenReturn(null);
        doNothing().when(codeMapper).insertCode(any(SecurityCode.class));

        var code = codeDao.insertCode(usr.getId());

        assertTrue(code > 0);

    }

    @Test
    public void insertCodeUserNotFoundTest() {
        when(userMapper.getUserById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class, () -> codeDao.insertCode(7));

    }


    @Test
    public void enterCodeTest() throws Exception {
        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        when(codeMapper.getCodeByUserId(anyLong())).thenReturn(null);

        doNothing().when(codeMapper).insertCode(any(SecurityCode.class));
        var code = codeDao.insertCode(usr.getId());
        when(codeMapper.getCodeByUserId(anyLong())).thenReturn(new SecurityCode(code, usr.getId()));
        doNothing().when(codeMapper).deleteCode(anyLong());
        codeDao.enterCode(usr.getId(), code);


    }

    @Test
    public void enterCodeUserNotFoundTest() {
        when(userMapper.getUserById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class, () -> codeDao.enterCode(7, 123));
    }

    @Test
    public void enterCodeNotFoundTest() throws Exception {
        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        when(codeMapper.getCodeByUserId(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class, () -> codeDao.enterCode(usr.getId(), 123));

    }

    @Test
    public void enterCodeNotMatchesTest() throws Exception {
        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        when(codeMapper.getCodeByUserId(anyLong())).thenReturn(null);

        doNothing().when(codeMapper).insertCode(any(SecurityCode.class));
        var code = codeDao.insertCode(usr.getId());
        when(codeMapper.getCodeByUserId(anyLong())).thenReturn(new SecurityCode(anyLong(), usr.getId()));

        assertThrows(DataExistenceException.class, () -> codeDao.enterCode(usr.getId(), 123));

    }


}
