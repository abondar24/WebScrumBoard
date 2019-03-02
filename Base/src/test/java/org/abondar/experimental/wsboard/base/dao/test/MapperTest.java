package org.abondar.experimental.wsboard.base.dao.test;

import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.base.dao.DataMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;



@SpringBootTest(classes= Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class MapperTest {
  private   static Logger logger = LoggerFactory.getLogger(MapperTest.class);

  @Autowired
  private DataMapper mapper;

  @Test
  public void insertUpdateUserTest(){
      logger.info("User insert/update test");
  }

}
