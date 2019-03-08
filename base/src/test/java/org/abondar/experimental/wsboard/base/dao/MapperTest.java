package org.abondar.experimental.wsboard.base.dao;

import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.datamodel.User;
import org.abondar.experimental.wsboard.datamodel.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@SpringBootTest(classes= Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class MapperTest {
  private   static Logger logger = LoggerFactory.getLogger(MapperTest.class);

  @Autowired
  private  DataMapper mapper;

  @Test
  public void insertUpdateUserTest(){
    logger.info("User insert test");

    String roles = UserRole.Developer + ":" + UserRole.Manager;
    User user = new User("testUser","test@email.com",
            "test","test","12345",roles);

    mapper.insertUpdateUser(user);
    logger.info("Created user with id:" +user.getId());
    assertTrue(user.getId()>0);

    mapper.deleteUsers();
  }

}
