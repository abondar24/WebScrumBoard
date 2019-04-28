package org.abondar.experimental.wsboard.test.dao.data.password;

import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.dao.password.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class PasswordTest {


    @Test
    public void createHashTest() throws Exception {
        var pwd = "testpWd";
        var pwdHash = PasswordUtil.createHash(pwd);

        assertTrue(PasswordUtil.verifyPassword(pwd, pwdHash));
    }

}
