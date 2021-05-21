package org.abondar.experimental.wsboard.server.dao;


import org.abondar.experimental.wsboard.server.datamodel.Contributor;
import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.abondar.experimental.wsboard.server.datamodel.user.UserRole;
import org.abondar.experimental.wsboard.server.exception.DataCreationException;
import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.junit.jupiter.api.Test;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


public class ContributorDaoTest extends BaseDaoTest {


    @Test
    public void createContributorTest() throws Exception {
        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getProjectOwner(anyLong())).thenReturn(null);
        when(mapper.getContributorByUserAndProject(anyLong(), anyLong())).thenReturn(null);
        doNothing().when(mapper).insertContributor(any(Contributor.class));
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), true);

        assertEquals(0, contr.getId());

    }

    @Test
    public void createContributorAlreadyExistsTest() throws Exception {
        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getContributorByUserAndProject(anyLong(), anyLong())).thenReturn(ctr);

        assertThrows(DataExistenceException.class, () -> contributorDao.createContributor(usr.getId(), prj.getId(), false));

    }

    @Test
    public void createContributorReactivateTest() throws Exception {
        var ctrId = ctr.getId();
        ctr.setActive(false);

        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getContributorByUserAndProject(anyLong(), anyLong())).thenReturn(ctr);
        doNothing().when(mapper).updateContributor(any(Contributor.class));

        var res = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        assertEquals(ctrId, res.getId());
        assertTrue(res.isActive());
        assertFalse(res.isOwner());

    }

    @Test
    public void createContributorReactivateIsOwnerTest() throws Exception {
        var ctrId = ctr.getId();
        ctr.setActive(false);

        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getContributorByUserAndProject(anyLong(), anyLong())).thenReturn(ctr);
        when(mapper.getProjectOwner(anyLong())).thenReturn(null);
        doNothing().when(mapper).updateContributor(any(Contributor.class));

        var res = contributorDao.createContributor(usr.getId(), prj.getId(), true);

        assertEquals(ctrId, res.getId());
        assertTrue(res.isActive());
        assertTrue(res.isOwner());

    }


    @Test
    public void createContributorNotOwnerTest() throws Exception {
        ctr.setActive(false);

        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getContributorByUserAndProject(anyLong(), anyLong())).thenReturn(ctr);
        doNothing().when(mapper).updateContributor(any(Contributor.class));

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        assertEquals(0, contr.getId());

    }


    @Test
    public void createContributorProjectNotActiveTest() throws Exception {
        prj.setActive(false);

        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(prj);

        assertThrows(DataCreationException.class, () ->
                contributorDao.createContributor(usr.getId(), prj.getId(), false));

    }

    @Test
    public void createContributorProjectHasOwnerTest() throws Exception {
        ctr.setOwner(true);

        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getProjectOwner(anyLong())).thenReturn(usr);

        assertThrows(DataCreationException.class, () ->
                contributorDao.createContributor(usr.getId(), prj.getId(), true));

    }


    @Test
    public void updateContributorAsOwnerTest() throws Exception {
        var id = ctr.getId();

        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getContributorByUserAndProject(anyLong(), anyLong())).thenReturn(ctr);
        when(mapper.getProjectOwner(prj.getId())).thenReturn(usr);
        when(mapper.getContributorByUserAndProject(usr.getId(),prj.getId())).thenReturn(ctr);
        doNothing().when(mapper).updateContributor(any(Contributor.class));

        var res = contributorDao.updateContributor(usr.getId(), prj.getId(), true, true);

        assertEquals(id, res.getId());

    }

    @Test
    public void updateContributorChangeOwnerTest() throws Exception {
        ctr.setOwner(true);
        var usr1 = new User("ctr1", "ctr1", "ctr1", "ctr1", "ctr1",
                UserRole.DEVELOPER.name() + ";");
        usr1.setId(1);

        var ctr1 = new Contributor(usr1.getId(), prj.getId(), false);

        when(mapper.getUserById(anyLong())).thenReturn(usr1);
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getContributorByUserAndProject(usr1.getId(), prj.getId())).thenReturn(ctr1);
        when(mapper.getContributorByUserAndProject(usr.getId(), prj.getId())).thenReturn(ctr);
        when(mapper.getProjectOwner(anyLong())).thenReturn(usr);
        doNothing().when(mapper).updateContributor(ctr);
        doNothing().when(mapper).updateContributor(ctr1);

        ctr1 = contributorDao.updateContributor(usr1.getId(), prj.getId(), true, true);

        when(mapper.getProjectOwner(prj.getId())).thenReturn(usr1);
        var res = contributorDao.findProjectOwner(prj.getId());

        when(mapper.getContributorById(ctr.getId())).thenReturn(ctr);
        var oldOwner = mapper.getContributorById(ctr.getId());

        assertEquals(ctr1.getUserId(), res.getId());
        assertTrue(ctr1.isOwner());
        assertFalse(oldOwner.isOwner());

    }


    @Test
    public void updateContributorNoUserTest() {
        when(mapper.getUserById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class,
                () -> contributorDao.updateContributor(100, prj.getId(), true, null));

    }

    @Test
    public void updateContributorNoProjectTest() throws Exception {
        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class,
                () -> contributorDao.updateContributor(usr.getId(), 100, true, null));

    }


    @Test
    public void updateInactiveContributorAsOwnerTest() {
        ctr.setActive(false);

        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getContributorByUserAndProject(usr.getId(), prj.getId())).thenReturn(ctr);
        when(mapper.getProjectOwner(anyLong())).thenReturn(usr);

        assertThrows(DataCreationException.class,
                () -> contributorDao.updateContributor(usr.getId(), prj.getId(), true, null));

    }

    @Test
    public void updateContributorOwnerAsInactiveTest() {
        ctr.setOwner(true);

        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getContributorByUserAndProject(usr.getId(), prj.getId())).thenReturn(ctr);

        assertThrows(DataCreationException.class,
                () -> contributorDao.updateContributor(usr.getId(), prj.getId(), null, false));

    }


    @Test
    public void updateContributorAsOwnerContributorNotExistsTest() {
        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getContributorByUserAndProject(anyLong(),anyLong())).thenReturn(null);

        assertThrows(DataExistenceException.class, () ->
                contributorDao.updateContributor(usr.getId(), prj.getId(), false, true));

    }

    @Test
    public void updateContributorAlreadyOwnerTest() {
        ctr.setOwner(true);
        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getContributorByUserAndProject(anyLong(),anyLong())).thenReturn(ctr);
        when(mapper.getProjectOwner(anyLong())).thenReturn(usr);

        assertThrows(DataCreationException.class, () ->
                contributorDao.updateContributor(usr.getId(), prj.getId(), true, true));

    }

    @Test
    public void updateContributorProjectHasNoOwnerTest()  {

        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getContributorByUserAndProject(anyLong(),anyLong())).thenReturn(ctr);
        when(mapper.getProjectOwner(anyLong())).thenReturn(null);

        assertThrows(DataCreationException.class, () ->
                contributorDao.updateContributor(usr.getId(), prj.getId(), false, true));

    }

    @Test
    public void updateContributorNullTest() throws Exception {
        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getContributorByUserAndProject(anyLong(),anyLong())).thenReturn(ctr);
        doNothing().when(mapper).updateContributor(ctr);

        var res = contributorDao.updateContributor(usr.getId(), prj.getId(), null, null);

        assertEquals(ctr.getId(), res.getId());

    }


    @Test
    public void findProjectOwnerTest() throws Exception {
        ctr.setOwner(true);

        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getProjectOwner(anyLong())).thenReturn(usr);

        var ownr = contributorDao.findProjectOwner(prj.getId());

        assertEquals(usr.getId(), ownr.getId());

    }

    @Test
    public void findProjectContributorsTest() throws Exception {
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getContributorsForProject(prj.getId(),0,1)).thenReturn(List.of(usr));

        var contrs = contributorDao.findProjectContributors(prj.getId(), 0, 1);

        assertEquals(1, contrs.size());

    }

    @Test
    public void findContributorByLoginTest() throws Exception {
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getContributorByLogin(anyLong(),anyString())).thenReturn(ctr);

        var res = contributorDao.findContributorByLogin(prj.getId(), usr.getLogin());

        assertEquals(ctr.getId(), res.getId());
    }

    @Test
    public void findContributorNotExistsByLoginTest()  {
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getContributorByLogin(anyLong(),anyString())).thenReturn(null);

        assertThrows(DataExistenceException.class, () ->
                contributorDao.findContributorByLogin(prj.getId(),"test"));
    }

    @Test
    public void findContributorByLoginProjectNotExistsTest()  {
        when(mapper.getProjectById(anyLong())).thenReturn(null);

        assertThrows(DataExistenceException.class, () ->
                contributorDao.findContributorByLogin(100, usr.getLogin()));
    }

    @Test
    public void countProjectContributorsTest() throws Exception {
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.countProjectContributors(anyLong())).thenReturn(1);

        var res = contributorDao.countProjectContributors(prj.getId());
        assertEquals(Integer.valueOf(1), res);

    }

    @Test
    public void countContributorsForProjectNotFoundTest() {
        when(mapper.getProjectById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class, () -> contributorDao.countProjectContributors(100L));
    }

    @Test
    public void findContributorsByUserIdTest() throws Exception {
        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getContributorsByUserId(usr.getId(),0,1)).thenReturn(List.of(ctr));

        var contrs = contributorDao.findContributorsByUserId(usr.getId(), 0, 1);

        assertEquals(1, contrs.size());
        assertEquals(ctr.getId(), contrs.get(0).getId());

    }

    @Test
    public void findContributorsByUserNotFoundTest() {
        when(mapper.getUserById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class,
                () -> contributorDao.findContributorsByUserId(7, 0, 1));

    }

    @Test
    public void findContributorByUserAndProjectTest() throws Exception {
        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(prj);
        when(mapper.getContributorByUserAndProject(anyLong(),anyLong())).thenReturn(ctr);

        var res = contributorDao.findContributorByUserAndProject(usr.getId(), prj.getId());
        assertTrue(res.isPresent());
        assertEquals(Long.valueOf(ctr.getId()), res.get());

    }


    @Test
    public void findContributorByUserNotFoundAndProjectTest()  {
        when(mapper.getUserById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class,
                () -> contributorDao.findContributorByUserAndProject(usr.getId(), 7));


    }

    @Test
    public void findContributorByUserAndProjectNotFoundTest() {
        when(mapper.getUserById(anyLong())).thenReturn(usr);
        when(mapper.getProjectById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class,
                () -> contributorDao.findContributorByUserAndProject(7, 7));

    }

}
