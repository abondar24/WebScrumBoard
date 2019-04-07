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


    @Test
    public void updateSprintTest() {
        logger.info("Update sprint test");
        var sp = sprintDao.createSprint("test", new Date(), new Date());

        var startDate = new Date();
        var endDate = new Date();
        var res = sprintDao.updateSprint(sp.getObject().getId(), null, startDate, endDate);

        assertEquals(startDate, res.getObject().getStartDate());
        assertEquals(endDate, res.getObject().getEndDate());

        mapper.deleteSprints();
    }


    @Test
    public void updateSprintNotFoundTest() {
        logger.info("Update sprint not found test");
        var res = sprintDao.updateSprint(100, null, null, null);

        assertEquals(ErrorMessageUtil.SPRINT_NOT_EXISTS, res.getMessage());

    }

    @Test
    public void updateSprintNameExistsTest() {
        logger.info("Update sprint name exists test");
        var sp = sprintDao.createSprint("test", new Date(), new Date());

        var res = sprintDao.updateSprint(sp.getObject().getId(), sp.getObject().getName(),
                null, null);

        assertEquals(ErrorMessageUtil.SPRINT_EXISTS, res.getMessage());

        mapper.deleteSprints();
    }

    @Test
    public void getSprintByIdTest() {
        logger.info("Get sprint by id test");
        var sp = sprintDao.createSprint("test", new Date(), new Date());

        var res = sprintDao.getSprintById(sp.getObject().getId());

        assertEquals(sp.getObject().getName(), res.getObject().getName());

        mapper.deleteSprints();
    }


    @Test
    public void getSprintsTest() {
        logger.info("Get sprints test");
        var sp = sprintDao.createSprint("test", new Date(), new Date());

        var res = sprintDao.getSprints(0, 1);

        assertEquals(1, res.getObject().size());
        assertEquals(sp.getObject().getName(), res.getObject().get(0).getName());

        mapper.deleteSprints();
    }


    @Test
    public void deleteSprintTest() {
        logger.info("Delete sprint test");
        var sp = sprintDao.createSprint("test", new Date(), new Date());

        var res = sprintDao.deleteSprint(sp.getObject().getId());

        assertTrue(res);
    }
}
