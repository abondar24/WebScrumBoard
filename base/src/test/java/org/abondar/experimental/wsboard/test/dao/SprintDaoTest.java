package org.abondar.experimental.wsboard.test.dao;


import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.dao.ProjectDao;
import org.abondar.experimental.wsboard.dao.SprintDao;
import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Autowired
    private DataMapper mapper;


    @Autowired
    @Qualifier("sprintDao")
    private SprintDao sprintDao;

    @Autowired
    @Qualifier("projectDao")
    private ProjectDao projectDao;


    @Test
    public void createSprintTest() throws Exception {
        var res = sprintDao.createSprint("test", new Date(), new Date(),createProject());

        assertNotNull(res);

        cleanData();
    }

    @Test
    public void createSprintWrongEndDateTest() {
        assertThrows(DataCreationException.class, () -> sprintDao.createSprint("test", new Date(),
                yesterday(),createProject()));

        cleanData();
    }


    @Test
    public void createSprintAlreadyExistsTest() throws Exception {
        String name = "test";
        sprintDao.createSprint(name, new Date(), new Date(),createProject());

        assertThrows(DataExistenceException.class, () -> sprintDao.createSprint(name, new Date(), new Date(),createProject()));

        cleanData();
    }

    @Test
    public void createSprintProjectNotExistsTest() throws Exception {
        String name = "test";
        sprintDao.createSprint(name, new Date(), new Date(),createProject());

        assertThrows(DataExistenceException.class, () -> sprintDao.createSprint(name, new Date(), new Date(),7));

        cleanData();
    }

    @Test
    public void createSprintBlankDataTest() {
        assertThrows(DataCreationException.class, () -> sprintDao.createSprint(null, new Date(), new Date(),createProject()));
        cleanData();
    }



    @Test
    public void updateSprintTest() throws Exception {
        var sp = sprintDao.createSprint("test", new Date(), new Date(),createProject());
        var id = sp.getId();
        var startDate = new Date();
        var endDate = new Date();
        var res = sprintDao.updateSprint(sp.getId(), null, startDate, endDate);

        assertEquals(id, res.getId());
        assertEquals(startDate, res.getStartDate());
        assertEquals(endDate, res.getEndDate());

        cleanData();
    }


    @Test
    public void updateSprintNotFoundTest() {
        assertThrows(DataExistenceException.class, () ->
                sprintDao.updateSprint(100, null, null, null));
    }

    @Test
    public void updateSprintNameExistsTest() throws Exception {
        var sp = sprintDao.createSprint("test", new Date(), new Date(),createProject());

        assertThrows(DataExistenceException.class, () ->
                sprintDao.updateSprint(sp.getId(), sp.getName(),
                        null, null));

        cleanData();
    }

    @Test
    public void updateSprintWrongEndDateTest() throws Exception {
        var sp = sprintDao.createSprint("test", new Date(), new Date(),createProject());

        assertThrows(DataCreationException.class, () ->
                sprintDao.updateSprint(sp.getId(), null,
                        new Date(), yesterday()));

        cleanData();
    }


    @Test
    public void getSprintByIdTest() throws Exception {
        var sp = sprintDao.createSprint("test", new Date(), new Date(),createProject());

        var res = sprintDao.getSprintById(sp.getId());

        assertEquals(sp.getName(), res.getName());

        cleanData();
    }


    @Test
    public void getSprintsTest() throws Exception {
        var sp = sprintDao.createSprint("test", new Date(), new Date(),createProject());

        var res = sprintDao.getSprints(0, 1);

        assertEquals(1, res.size());
        assertEquals(sp.getName(), res.get(0).getName());

        cleanData();
    }


    @Test
    public void deleteSprintTest() throws Exception {
        var sp = sprintDao.createSprint("test", new Date(), new Date(),createProject());

        sprintDao.deleteSprint(sp.getId());

        assertThrows(DataExistenceException.class, () -> sprintDao.getSprintById(sp.getId()));

        cleanData();

    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    private long createProject() throws Exception {
        var name = "test";
        var startDate = new Date();

        return projectDao.createProject(name, startDate).getId();
    }

    private void cleanData(){
        mapper.deleteSprints();
        mapper.deleteProjects();
    }
}
