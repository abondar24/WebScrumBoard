package org.abondar.experimental.wsboard.common.util;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class PasswordUtilTest {


    @Test
    public void createHashTest() throws Exception {
        var pwd = "testpWd";
        var pwdHash = PasswordUtil.createHash(pwd);

        assertTrue(PasswordUtil.verifyPassword(pwd, pwdHash));
    }

}
