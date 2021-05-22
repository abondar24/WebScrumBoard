package org.abondar.experimental.wsboard.server.dao;


import org.abondar.experimental.wsboard.server.datamodel.Sprint;
import org.abondar.experimental.wsboard.server.exception.DataCreationException;
import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


public class SprintDaoTest extends BaseDaoTest {

    @InjectMocks
    private SprintDao sprintDao;


    @Test
    public void createSprintTest() throws Exception {
        when(mapper.getSprintByName(anyString())).thenReturn(null);
        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        doNothing().when(mapper).insertSprint(any(Sprint.class));

        var res = sprintDao.createSprint(sp.getName(), sp.getStartDate(), sp.getEndDate(), sp.getProjectId());

        assertEquals(sp.getName(), res.getName());

    }

    @Test
    public void createSprintWrongEndDateTest() {
        when(mapper.getSprintByName(anyString())).thenReturn(null);
        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        assertThrows(DataCreationException.class, () -> sprintDao.createSprint(sp.getName(), sp.getStartDate(),
                yesterday(), sp.getProjectId()));

    }


    @Test
    public void createSprintAlreadyExistsTest() {
        when(mapper.getSprintByName(anyString())).thenReturn(sp);
        assertThrows(DataExistenceException.class, () ->
                sprintDao.createSprint(sp.getName(), sp.getStartDate(), sp.getEndDate(), sp.getProjectId()));

    }

    @Test
    public void createSprintProjectNotExistsTest() throws Exception {
        when(projectMapper.getProjectById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class, () ->
                sprintDao.createSprint(sp.getName(), sp.getStartDate(), sp.getEndDate(), 7));

    }

    @Test
    public void createSprintBlankDataTest() {
        when(mapper.getSprintByName(null)).thenReturn(null);
        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        assertThrows(DataCreationException.class, () ->
                sprintDao.createSprint(null, sp.getStartDate(), sp.getEndDate(), sp.getProjectId()));

    }


    @Test
    public void updateSprintTest() throws Exception {
        var id = sp.getId();
        var startDate = new Date();
        var endDate = new Date();

        when(mapper.getSprintById(anyLong())).thenReturn(sp);
        when(mapper.getCurrentSprint(sp.getProjectId())).thenReturn(null);
        doNothing().when(mapper).updateSprint(any(Sprint.class));

        var res = sprintDao.updateSprint(id, null, startDate, endDate, true);

        assertEquals(id, res.getId());
        assertEquals(startDate, res.getStartDate());
        assertEquals(endDate, res.getEndDate());

    }


    @Test
    public void updateSprintNotFoundTest() {
        when(mapper.getSprintById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class, () ->
                sprintDao.updateSprint(100, null, null, null, null));
    }

    @Test
    public void updateSprintNameExistsTest()  {
        when(mapper.getSprintById(anyLong())).thenReturn(sp);
        when(mapper.getSprintByName(sp.getName())).thenReturn(sp);
        assertThrows(DataExistenceException.class, () ->
                sprintDao.updateSprint(sp.getId(), sp.getName(), null, null, null));

    }

    @Test
    public void updateSprintWrongEndDateTest() {
        when(mapper.getSprintById(anyLong())).thenReturn(sp);
        assertThrows(DataCreationException.class, () ->
                sprintDao.updateSprint(sp.getId(), null, new Date(), yesterday(), null));

    }

    @Test
    public void updateSprintActiveExistsTest() throws Exception {
        sp.setCurrent(true);

        when(mapper.getSprintById(anyLong())).thenReturn(sp);
        when(mapper.getCurrentSprint(anyLong())).thenReturn(sp);

        assertThrows(DataExistenceException.class, () ->
                sprintDao.updateSprint(sp.getId(), null, null, null, true));
    }


    @Test
    public void getSprintByIdTest() throws Exception {
        when(mapper.getSprintById(anyLong())).thenReturn(sp);
        var res = sprintDao.getSprintById(sp.getId());

        assertEquals(sp.getName(), res.getName());

    }


    @Test
    public void getSprintsTest() throws Exception {
        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getSprints(sp.getProjectId(),0,1)).thenReturn(List.of(sp));

        var res = sprintDao.getSprints(prj.getId(), 0, 1);

        assertEquals(1, res.size());
        assertEquals(sp.getName(), res.get(0).getName());

    }

    @Test
    public void getCurrentSprintTest() throws Exception {
        sp.setCurrent(true);
        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getCurrentSprint(anyLong())).thenReturn(sp);

        var res = sprintDao.getCurrentSprint(prj.getId());

        assertEquals(sp.getId(), res.getId());

    }

    @Test
    public void getCurrentSprintProjectNotExistsTest() throws Exception {
        when(projectMapper.getProjectById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class, () -> sprintDao.getCurrentSprint(100));

    }

    @Test
    public void getCurrentSprintNullTest() throws Exception {
        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getCurrentSprint(anyLong())).thenReturn(null);
        var res = sprintDao.getCurrentSprint(prj.getId());

        assertNull(res);

    }

    @Test
    public void countSprintsTest() throws Exception {
        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.countSprints(prj.getId())).thenReturn(1);
        var res = sprintDao.countSprints(prj.getId());

        assertEquals(Integer.valueOf(1), res);

    }

    @Test
    public void countSprintsProjectNotTest() {
        when(projectMapper.getProjectById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class, () -> {
            sprintDao.countSprints(100L);
        });
    }

    @Test
    public void getSprintsProjectNotFoundTest() throws Exception {
        when(projectMapper.getProjectById(anyLong())).thenReturn(null);

        assertThrows(DataExistenceException.class, () -> sprintDao.getSprints(7, 0, 1));

    }


    @Test
    public void deleteSprintTest() throws Exception {
        when(mapper.getSprintById(anyLong())).thenReturn(sp);
        doNothing().when(mapper).deleteSprint(anyLong());
        sprintDao.deleteSprint(sp.getId());

        when(mapper.getSprintById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class, () -> sprintDao.getSprintById(sp.getId()));

    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }


}
