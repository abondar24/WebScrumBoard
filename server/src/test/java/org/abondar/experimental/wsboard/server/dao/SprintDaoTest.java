package org.abondar.experimental.wsboard.server.dao;



import org.abondar.experimental.wsboard.server.exception.DataCreationException;
import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;



public class SprintDaoTest extends BaseDaoTest {


    @Test
    public void createSprintTest() throws Exception {
        cleanData();
        var res = sprintDao.createSprint("test", new Date(), new Date(), createProject().getId());

        assertNotNull(res);


    }

    @Test
    public void createSprintWrongEndDateTest() {
        cleanData();
        assertThrows(DataCreationException.class, () -> sprintDao.createSprint("test", new Date(),
                yesterday(), createProject().getId()));

    }


    @Test
    public void createSprintAlreadyExistsTest() throws Exception {

        cleanData();
        String name = "test";
        sprintDao.createSprint(name, new Date(), new Date(), createProject().getId());

        assertThrows(DataExistenceException.class, () ->
                sprintDao.createSprint(name, new Date(), new Date(), createProject().getId()));

    }

    @Test
    public void createSprintProjectNotExistsTest() throws Exception {
        cleanData();
        String name = "test";
        sprintDao.createSprint(name, new Date(), new Date(), createProject().getId());

        assertThrows(DataExistenceException.class, () ->
                sprintDao.createSprint(name, new Date(), new Date(), 7));

        cleanData();
    }

    @Test
    public void createSprintBlankDataTest() {
        cleanData();
        assertThrows(DataCreationException.class, () ->
                sprintDao.createSprint(null, new Date(), new Date(), createProject().getId()));

    }


    @Test
    public void updateSprintTest() throws Exception {
        cleanData();

        var sp = sprintDao.createSprint("test", new Date(), new Date(), createProject().getId());
        var id = sp.getId();
        var startDate = new Date();
        var endDate = new Date();
        var res = sprintDao.updateSprint(sp.getId(), null, startDate, endDate, true);

        assertEquals(id, res.getId());
        assertEquals(startDate, res.getStartDate());
        assertEquals(endDate, res.getEndDate());

    }


    @Test
    public void updateSprintNotFoundTest() {
        cleanData();
        assertThrows(DataExistenceException.class, () ->
                sprintDao.updateSprint(100, null, null, null, null));
    }

    @Test
    public void updateSprintNameExistsTest() throws Exception {
        cleanData();
        var sp = sprintDao.createSprint("test", new Date(), new Date(), createProject().getId());

        assertThrows(DataExistenceException.class, () ->
                sprintDao.updateSprint(sp.getId(), sp.getName(), null, null, null));

    }

    @Test
    public void updateSprintWrongEndDateTest() throws Exception {
        cleanData();
        var sp = sprintDao.createSprint("test", new Date(), new Date(), createProject().getId());

        assertThrows(DataCreationException.class, () ->
                sprintDao.updateSprint(sp.getId(), null, new Date(), yesterday(), null));

    }

    @Test
    public void updateSprintActiveExistsTest() throws Exception {
        cleanData();

        var sp = sprintDao.createSprint("test", new Date(), new Date(), createProject().getId());
        sprintDao.updateSprint(sp.getId(), null, null, null, true);


        assertThrows(DataExistenceException.class, () ->
                sprintDao.updateSprint(sp.getId(), null, null, null, true));
    }


    @Test
    public void getSprintByIdTest() throws Exception {
        cleanData();
        var sp = sprintDao.createSprint("test", new Date(), new Date(), createProject().getId());

        var res = sprintDao.getSprintById(sp.getId());

        assertEquals(sp.getName(), res.getName());

    }


    @Test
    public void getSprintsTest() throws Exception {
        cleanData();

        var prj = createProject();
        var sp = sprintDao.createSprint("test", new Date(), new Date(), prj.getId());

        var res = sprintDao.getSprints(prj.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(sp.getName(), res.get(0).getName());

    }

    @Test
    public void getCurrentSprintTest() throws Exception {
        cleanData();

        var prj = createProject();
        var sp = sprintDao.createSprint("test", new Date(), new Date(), prj.getId());
        sp = sprintDao.updateSprint(sp.getId(), null, null, null, true);

        var res = sprintDao.getCurrentSprint(prj.getId());

        assertEquals(sp.getId(), res.getId());

    }

    @Test
    public void getCurrentSprintProjectNotExistsTest() throws Exception {
        cleanData();

        var prj = createProject();
        var sp = sprintDao.createSprint("test", new Date(), new Date(), prj.getId());
        sprintDao.updateSprint(sp.getId(), null, null, null, true);

        assertThrows(DataExistenceException.class, () -> sprintDao.getCurrentSprint(100));

    }

    @Test
    public void getCurrentSprintNullTest() throws Exception {
        cleanData();

        var prj = createProject();
        sprintDao.createSprint("test", new Date(), new Date(), prj.getId());

        var res = sprintDao.getCurrentSprint(prj.getId());

        assertNull(res);

    }

    @Test
    public void countSprintsTest() throws Exception {
        cleanData();

        var prj = createProject();
        sprintDao.createSprint("test", new Date(), new Date(), prj.getId());

        var res = sprintDao.countSprints(prj.getId());

        assertEquals(Integer.valueOf(1), res);

    }

    @Test
    public void countSprintsProjectNotTest() {
        cleanData();
        assertThrows(DataExistenceException.class, () -> {
            sprintDao.countSprints(100L);
        });
    }

    @Test
    public void getSprintsProjectNotFoundTest() throws Exception {
        cleanData();

        var prj = createProject();
        sprintDao.createSprint("test", new Date(), new Date(), prj.getId());

        assertThrows(DataExistenceException.class, () -> sprintDao.getSprints(7, 0, 1));

    }


    @Test
    public void deleteSprintTest() throws Exception {
        cleanData();
        var sp = sprintDao.createSprint("test", new Date(), new Date(), createProject().getId());

        sprintDao.deleteSprint(sp.getId());

        assertThrows(DataExistenceException.class, () -> sprintDao.getSprintById(sp.getId()));

    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }


}
