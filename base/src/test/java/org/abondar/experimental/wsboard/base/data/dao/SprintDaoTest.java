package org.abondar.experimental.wsboard.base.data.dao;


import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.ErrorMessageUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class SprintDaoTest {

    private static Logger logger = LoggerFactory.getLogger(SprintDaoTest.class);

    @Autowired
    private DataMapper mapper;

    @Autowired
    @Qualifier("taskDao")
    private TaskDao dao;

    @Autowired
    @Qualifier("userDao")
    private UserDao userDao;

    @Autowired
    @Qualifier("projectDao")
    private ProjectDao projectDao;

    @Autowired
    @Qualifier("contributorDao")
    private ContributorDao contributorDao;

    @Autowired
    @Qualifier("sprintDao")
    private SprintDao sprintDao;


    @Test
    public void createSprintTest() {
        logger.info("Create sprint test");

        var res = sprintDao.createSprint("test", new Date(), new Date());

        assertNull(res.getMessage());
        assertNotNull(res.getObject());

        mapper.deleteSprints();
    }

    @Test
    public void createSprintAlreadyExistsTest() {
        logger.info("Create sprint already exists test");

        String name = "test";
        sprintDao.createSprint(name, new Date(), new Date());

        var res = sprintDao.createSprint(name, new Date(), new Date());

        assertEquals(ErrorMessageUtil.SPRINT_EXISTS, res.getMessage());

        mapper.deleteSprints();
    }


}
