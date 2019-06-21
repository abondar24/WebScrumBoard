package org.abondar.experimental.wsboard.test.dao;

import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.dao.ContributorDao;
import org.abondar.experimental.wsboard.dao.ProjectDao;
import org.abondar.experimental.wsboard.dao.UserDao;
import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(classes = WebScrumBoardApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ContributorDaoTest {


    @Autowired
    private DataMapper mapper;

    @Autowired
    @Qualifier("contributorDao")
    private ContributorDao contributorDao;

    @Autowired
    @Qualifier("userDao")
    private UserDao userDao;

    @Autowired
    @Qualifier("projectDao")
    private ProjectDao projectDao;


    @Test
    public void createContributorTest() throws Exception {
        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), true);

        assertTrue(contr.getId() > 0);

        cleanData();
    }


    @Test
    public void createContributorNotOwnerTest() throws Exception {
        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        assertTrue(contr.getId() > 0);

        cleanData();
    }


    @Test
    public void createContributorActiveFalseTest() throws Exception {
        var usr = createUser();
        var prj = createProject(false);

        assertThrows(DataCreationException.class, () ->
                contributorDao.createContributor(usr.getId(), prj.getId(), false));

        cleanData();
    }

    @Test
    public void createContributorProjectHasOwnerTest() throws Exception {
        var usr = createUser();
        var prj = createProject(true);

        contributorDao.createContributor(usr.getId(), prj.getId(), true);

        assertThrows(DataCreationException.class, () ->
                contributorDao.createContributor(usr.getId(), prj.getId(), true));

        cleanData();
    }


    @Test
    public void updateContributorAsOwnerTest() throws Exception {
        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var id = contr.getId();
        contr = contributorDao.updateContributor(contr.getId(), true, true);

        assertEquals(id, contr.getId());

        cleanData();
    }


    @Test
    public void updateInactiveContributorAsOwnerTest() throws Exception {
        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var id = contr.getId();
        contributorDao.updateContributor(contr.getId(), null, false);

        assertThrows(DataCreationException.class, () -> contributorDao.updateContributor(id, true, null));


        cleanData();
    }

    @Test
    public void updateContributorOwnerAsInactiveTest() throws Exception {
        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), true);
        var id = contr.getId();

        assertThrows(DataCreationException.class, () -> contributorDao.updateContributor(id, null, false));


        cleanData();
    }


    @Test
    public void updateContributorAsOwnerContributorNotExistsTest() {
        assertThrows(DataExistenceException.class, () ->
                contributorDao.updateContributor(100, false, true));

        cleanData();
    }

    @Test
    public void updateContributorProjectHasOwnerTest() throws Exception {
        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), true);

        assertThrows(DataCreationException.class, () ->
                contributorDao.updateContributor(contr.getId(), true, true));


        cleanData();
    }

    @Test
    public void updateContributorProjectHasNoOwnerTest() throws Exception {
        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        assertThrows(DataCreationException.class, () ->
                contributorDao.updateContributor(contr.getId(), false, true));


        cleanData();
    }

    @Test
    public void updateContributorNullTest() throws Exception {
        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);
        var id = contr.getId();

        contr = contributorDao.updateContributor(id, null, null);

        assertEquals(id, contr.getId());

        cleanData();
    }


    @Test
    public void findProjectOwnerTest() throws Exception {
        var usr = createUser();
        var prj = createProject(true);

        contributorDao.createContributor(usr.getId(), prj.getId(), true);

        var ownr = contributorDao.findProjectOwner(prj.getId());

        assertEquals(usr.getId(), ownr.getId());

        cleanData();
    }

    @Test
    public void findContributorsForProjectTest() throws Exception {
        var usr = createUser();
        var prj = createProject(true);

        contributorDao.createContributor(usr.getId(), prj.getId(), true);
        var contrs = contributorDao.findProjectContributors(prj.getId(), 0, 1);

        assertEquals(1, contrs.size());


        cleanData();
    }


    private User createUser() throws Exception {
        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name();

        return userDao.createUser(login, password, email, firstName, lastName, roles);
    }

    private Project createProject(boolean isActive) throws Exception {
        var name = "test";
        var startDate = new Date();

        var prj = projectDao.createProject(name, startDate);
        if (!isActive) {
            prj = projectDao.updateProject(prj.getId(), null, null, false, new Date());
        }

        return prj;
    }

    private void cleanData() {
        mapper.deleteContributors();
        mapper.deleteProjects();
        mapper.deleteUsers();
    }
}
