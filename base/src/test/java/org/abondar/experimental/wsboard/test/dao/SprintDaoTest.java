package org.abondar.experimental.wsboard.test.dao;


import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.dao.SprintDao;
import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest(classes = WebScrumBoardApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class SprintDaoTest {

    private static Logger logger = LoggerFactory.getLogger(SprintDaoTest.class);

    @Autowired
    private DataMapper mapper;


    @Autowired
    @Qualifier("sprintDao")
    private SprintDao sprintDao;


    @Test
    public void createSprintTest() throws Exception {
        logger.info("Create sprint test");

        var res = sprintDao.createSprint("test", new Date(), new Date());

        assertNotNull(res);

        mapper.deleteSprints();
    }

    @Test
    public void createSprintWrongEndDateTest() {
        logger.info("Create sprint end date test");

        assertThrows(DataCreationException.class, () -> sprintDao.createSprint("test", new Date(), yesterday()));

        mapper.deleteSprints();
    }


    @Test
    public void createSprintAlreadyExistsTest() throws Exception {
        logger.info("Create sprint already exists test");

        String name = "test";
        sprintDao.createSprint(name, new Date(), new Date());


        assertThrows(DataExistenceException.class, () -> sprintDao.createSprint(name, new Date(), new Date()));

        mapper.deleteSprints();
    }

    @Test
    public void createSprintBlankDataTest() {
        logger.info("Create sprint blank data test");

        assertThrows(DataCreationException.class, () -> sprintDao.createSprint(null, new Date(), new Date()));
        mapper.deleteSprints();
    }



    @Test
    public void updateSprintTest() throws Exception {
        logger.info("Update sprint test");
        var sp = sprintDao.createSprint("test", new Date(), new Date());
        var id = sp.getId();
        var startDate = new Date();
        var endDate = new Date();
        var res = sprintDao.updateSprint(sp.getId(), null, startDate, endDate);

        assertEquals(id, res.getId());
        assertEquals(startDate, res.getStartDate());
        assertEquals(endDate, res.getEndDate());

        mapper.deleteSprints();
    }


    @Test
    public void updateSprintNotFoundTest() {
        logger.info("Update sprint not found test");

        assertThrows(DataExistenceException.class, () ->
                sprintDao.updateSprint(100, null, null, null));
    }

    @Test
    public void updateSprintNameExistsTest() throws Exception {
        logger.info("Update sprint name exists test");
        var sp = sprintDao.createSprint("test", new Date(), new Date());

        assertThrows(DataExistenceException.class, () ->
                sprintDao.updateSprint(sp.getId(), sp.getName(),
                        null, null));

        mapper.deleteSprints();
    }

    @Test
    public void updateSprintWrongEndDateTest() throws Exception {
        logger.info("Update sprint name exists test");
        var sp = sprintDao.createSprint("test", new Date(), new Date());

        assertThrows(DataCreationException.class, () ->
                sprintDao.updateSprint(sp.getId(), null,
                        new Date(), yesterday()));

        mapper.deleteSprints();
    }


    @Test
    public void getSprintByIdTest() throws Exception {
        logger.info("Get sprint by id test");
        var sp = sprintDao.createSprint("test", new Date(), new Date());

        var res = sprintDao.getSprintById(sp.getId());

        assertEquals(sp.getName(), res.getName());

        mapper.deleteSprints();
    }


    @Test
    public void getSprintsTest() throws Exception {
        logger.info("Get sprints test");
        var sp = sprintDao.createSprint("test", new Date(), new Date());

        var res = sprintDao.getSprints(0, 1);

        assertEquals(1, res.size());
        assertEquals(sp.getName(), res.get(0).getName());

        mapper.deleteSprints();
    }


    @Test
    public void deleteSprintTest() throws Exception {
        logger.info("Delete sprint test");
        var sp = sprintDao.createSprint("test", new Date(), new Date());

        sprintDao.deleteSprint(sp.getId());

        assertThrows(DataExistenceException.class, () -> sprintDao.getSprintById(sp.getId()));

    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }
}
