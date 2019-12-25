package org.abondar.experimental.wsboard.test.dao;

import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ContributorDaoTest extends BaseDaoTest {


    @Test
    public void createContributorTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), true);

        assertTrue(contr.getId() > 0);

    }

    @Test
    public void createContributorAlreadyExistsTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);

        contributorDao.createContributor(usr.getId(), prj.getId(), false);

        assertThrows(DataExistenceException.class, () -> contributorDao.createContributor(usr.getId(), prj.getId(), false));

    }

    @Test
    public void createContributorReactivateTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);

        var ctr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var ctrId = ctr.getId();
        contributorDao.updateContributor(usr.getId(), prj.getId(), null, false);

        ctr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        assertEquals(ctrId, ctr.getId());
        assertTrue(ctr.isActive());
        assertFalse(ctr.isOwner());

    }

    @Test
    public void createContributorReactivateIsOwnerTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);

        var ctr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var ctrId = ctr.getId();
        contributorDao.updateContributor(usr.getId(), prj.getId(), null, false);

        ctr = contributorDao.createContributor(usr.getId(), prj.getId(), true);

        assertEquals(ctrId, ctr.getId());
        assertTrue(ctr.isActive());
        assertTrue(ctr.isOwner());

    }


    @Test
    public void createContributorNotOwnerTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        assertTrue(contr.getId() > 0);

    }


    @Test
    public void createContributorActiveFalseTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(false);
        projectDao.updateProject(prj.getId(), "", "", false, new Date(), "");

        assertThrows(DataCreationException.class, () ->
                contributorDao.createContributor(usr.getId(), prj.getId(), false));

    }

    @Test
    public void createContributorProjectHasOwnerTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);

        contributorDao.createContributor(usr.getId(), prj.getId(), true);

        assertThrows(DataCreationException.class, () ->
                contributorDao.createContributor(usr.getId(), prj.getId(), true));

    }


    @Test
    public void updateContributorAsOwnerTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var id = contr.getId();
        contr = contributorDao.updateContributor(usr.getId(), prj.getId(), true, true);

        assertEquals(id, contr.getId());

    }

    @Test
    public void updateContributorChangeOwnerTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);

        var ctr = contributorDao.createContributor(usr.getId(), prj.getId(), true);

        var usr1 = userDao.createUser("ctr1", "ctr1", "ctr1", "ctr1", "ctr1",
                UserRole.DEVELOPER.name() + ";");
        var contr1 = contributorDao.createContributor(usr1.getId(), prj.getId(), false);

        contr1 = contributorDao.updateContributor(usr1.getId(), prj.getId(), true, true);
        var res = contributorDao.findProjectOwner(prj.getId());

        var oldOwner = mapper.getContributorById(ctr.getId());

        assertEquals(contr1.getUserId(), res.getId());
        assertTrue(contr1.isOwner());
        assertFalse(oldOwner.isOwner());

    }


    @Test
    public void updateContributorNoUserTest() throws Exception {
        cleanData();

        var prj = createProject(true);


        assertThrows(DataExistenceException.class,
                () -> contributorDao.updateContributor(100, prj.getId(), true, null));

    }

    @Test
    public void updateContributorNoProjectTest() throws Exception {
        cleanData();

        var usr = createUser();

        assertThrows(DataExistenceException.class,
                () -> contributorDao.updateContributor(usr.getId(), 100, true, null));

    }


    @Test
    public void updateInactiveContributorAsOwnerTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);

        contributorDao.createContributor(usr.getId(), prj.getId(), false);
        contributorDao.updateContributor(usr.getId(), prj.getId(), null, false);

        assertThrows(DataCreationException.class,
                () -> contributorDao.updateContributor(usr.getId(), prj.getId(), true, null));

    }

    @Test
    public void updateContributorOwnerAsInactiveTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);

        contributorDao.createContributor(usr.getId(), prj.getId(), true);

        assertThrows(DataCreationException.class,
                () -> contributorDao.updateContributor(usr.getId(), prj.getId(), null, false));

    }


    @Test
    public void updateContributorAsOwnerContributorNotExistsTest() {
        cleanData();

        assertThrows(DataExistenceException.class, () ->
                contributorDao.updateContributor(100, 100, false, true));

    }

    @Test
    public void updateContributorProjectHasOwnerTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);

        contributorDao.createContributor(usr.getId(), prj.getId(), true);

        assertThrows(DataCreationException.class, () ->
                contributorDao.updateContributor(usr.getId(), prj.getId(), true, true));

    }

    @Test
    public void updateContributorProjectHasNoOwnerTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);

        contributorDao.createContributor(usr.getId(), prj.getId(), false);

        assertThrows(DataCreationException.class, () ->
                contributorDao.updateContributor(usr.getId(), prj.getId(), false, true));

    }

    @Test
    public void updateContributorNullTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var id = contr.getId();

        contr = contributorDao.updateContributor(usr.getId(), prj.getId(), null, null);

        assertEquals(id, contr.getId());

    }


    @Test
    public void findProjectOwnerTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);

        contributorDao.createContributor(usr.getId(), prj.getId(), true);

        var ownr = contributorDao.findProjectOwner(prj.getId());

        assertEquals(usr.getId(), ownr.getId());

    }

    @Test
    public void findContributorsForProjectTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);

        contributorDao.createContributor(usr.getId(), prj.getId(), true);
        var contrs = contributorDao.findProjectContributors(prj.getId(), 0, 1);

        assertEquals(1, contrs.size());

    }

    @Test
    public void findContributorByLoginTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);
        var ctr = contributorDao.createContributor(usr.getId(), prj.getId(), true);

        var res = contributorDao.findContributorByLogin(prj.getId(), usr.getLogin());

        assertEquals(ctr.getId(), res.getId());
    }

    @Test
    public void findContributorNotExistsByLoginTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);
        contributorDao.createContributor(usr.getId(), prj.getId(), true);

        assertThrows(DataExistenceException.class, () ->
                contributorDao.findContributorByLogin(prj.getId(),"test"));
    }

    @Test
    public void findContributorByNameProjectNotExistsTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);
        contributorDao.createContributor(usr.getId(), prj.getId(), true);

        assertThrows(DataExistenceException.class, () ->
                contributorDao.findContributorByLogin(100, usr.getLogin()));
    }

    @Test
    public void countContributorsForProjectTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);

        contributorDao.createContributor(usr.getId(), prj.getId(), true);

        var res = contributorDao.countProjectContributors(prj.getId());
        assertEquals(Integer.valueOf(1), res);

    }

    @Test
    public void countContributorsForProjectNotFoundTest() {
        cleanData();
        assertThrows(DataExistenceException.class, () -> contributorDao.countProjectContributors(100L));
    }

    @Test
    public void findContributorsByUserIdTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);
        var ctr = contributorDao.createContributor(usr.getId(), prj.getId(), true);

        var contrs = contributorDao.findContributorsByUserId(usr.getId(), 0, 1);

        assertEquals(1, contrs.size());
        assertEquals(ctr.getId(), contrs.get(0).getId());

    }

    @Test
    public void findContributorsByUserNotFoundTest() {
        cleanData();
        assertThrows(DataExistenceException.class,
                () -> contributorDao.findContributorsByUserId(7, 0, 1));

    }

    @Test
    public void findContributorByUserAndProjectTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject(true);
        var ctr = contributorDao.createContributor(usr.getId(), prj.getId(), true);

        var res = contributorDao.findContributorByUserAndProject(usr.getId(), prj.getId());
        assertEquals(Long.valueOf(ctr.getId()), res.get());

    }


    @Test
    public void findContributorByUserNotFoundAndProjectTest() throws Exception {
        cleanData();
        var usr = createUser();

        assertThrows(DataExistenceException.class,
                () -> contributorDao.findContributorByUserAndProject(usr.getId(), 7));


    }

    @Test
    public void findContributorByUserAndProjectNotFoundTest() {
        cleanData();
        assertThrows(DataExistenceException.class,
                () -> contributorDao.findContributorByUserAndProject(7, 7));

    }

}
