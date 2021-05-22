package org.abondar.experimental.wsboard.server.mapper;

import org.abondar.experimental.wsboard.server.datamodel.SecurityCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SecurityMapperTest extends MapperTest{

    @Test
    public void insertCodeTest() {
        cleanData();
        var user = createUser();

        var code = new SecurityCode(123345, user.getId());
        securityCodeMapper.insertCode(code);

        assertTrue(code.getId() > 0);

    }

    @Test
    public void deleteCodeTest() {
        cleanData();
        var user = createUser();

        var code = new SecurityCode(123345, user.getId());
        securityCodeMapper.insertCode(code);

        securityCodeMapper.deleteCode(code.getId());
        code = securityCodeMapper.getCodeByUserId(user.getId());

        assertNull(code);

    }

    @Test
    public void checkCodeExistsTest() {
        cleanData();
        var user = createUser();

        var code = new SecurityCode(123345, user.getId());
        securityCodeMapper.insertCode(code);

        Integer exists = securityCodeMapper.checkCodeExists(code.getCode());

        assertEquals(Integer.valueOf(1), exists);

    }

}
