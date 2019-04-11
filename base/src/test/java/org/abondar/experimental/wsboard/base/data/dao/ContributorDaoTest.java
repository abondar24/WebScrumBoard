package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.base.data.MapperTest;
import org.abondar.experimental.wsboard.base.data.ObjectWrapper;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.User;
import org.abondar.experimental.wsboard.datamodel.UserRole;
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
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ContributorDaoTest {

    private static Logger logger = LoggerFactory.getLogger(MapperTest.class);

    @Autowired
    private DataMapper mapper;

    @Autowired
    @Qualifier("contributorDao")
    private ContributorDao contributorDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProjectDao projectDao;

    @Test
    public void createContributorTest() throws Exception {
        logger.info("Create contributor test");

        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), true);

        assertNull(contr.getMessage());
        assertTrue(contr.getObject().getId() > 0);

        cleanData();
    }


    @Test
    public void createContributorNotOwnerTest() throws Exception {
        logger.info("Create contributor not owner test");

        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);

        assertNull(contr.getMessage());
        assertTrue(contr.getObject().getId() > 0);

        cleanData();
    }


    @Test
    public void createContributorActiveFalseTest() throws Exception {
        logger.info("Create contributor active false test");

        var usr = createUser();
        var prj = createProject(false);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);

        assertEquals(ErrorMessageUtil.PROJECT_NOT_ACTIVE, contr.getMessage());
        assertNull(contr.getObject());

        cleanData();
    }

    @Test
    public void createContributorProjectHasOwnerTest() throws Exception {
        logger.info("Create contributor project has owner test");

        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), true);
        contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), true);

        assertEquals(ErrorMessageUtil.PROJECT_HAS_OWNER, contr.getMessage());
        assertNull(contr.getObject());

        cleanData();
    }


    @Test
    public void updateContributorAsOwnerTest() throws Exception {
        logger.info("Update contributor as owner test");

        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        contr = contributorDao.updateContributorAsOwner(contr.getObject().getId(), true, true);

        assertNull(contr.getMessage());

        cleanData();
    }


    @Test
    public void updateContributorAsOwnerContributorNotExistsTest() {
        logger.info("Update contributor as owner contributor not exists");

        var contr = contributorDao.updateContributorAsOwner(100, false, true);

        assertEquals(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS, contr.getMessage());
        assertNull(contr.getObject());

        cleanData();
    }

    @Test
    public void updateContributorPrtojectHasOwnerTest() throws Exception {
        logger.info("Update contributor project has owner test");

        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), true);
        contr = contributorDao.updateContributorAsOwner(contr.getObject().getId(), true, true);

        assertEquals(ErrorMessageUtil.PROJECT_HAS_OWNER, contr.getMessage());
        assertNull(contr.getObject());

        cleanData();
    }

    @Test
    public void updateContributorProjectHasNoOwnerTest() throws Exception {
        logger.info("Update contributor project has no owner test");

        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        contr = contributorDao.updateContributorAsOwner(contr.getObject().getId(), false, true);

        assertEquals(ErrorMessageUtil.PROJECT_HAS_NO_OWNER, contr.getMessage());
        assertNull(contr.getObject());

        cleanData();
    }

    @Test
    public void updateContributorNullTest() throws Exception {
        logger.info("update contributor null test");

        var usr = createUser();
        var prj = createProject(true);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        contr = contributorDao.updateContributorAsOwner(contr.getObject().getId(), null, null);

        assertNull(contr.getMessage());

        cleanData();
    }


    @Test
    public void findProjectOwnerTest() throws Exception {
        logger.info("Find project owner test");

        var usr = createUser();
        var prj = createProject(true);

        contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), true);

        var ownr = contributorDao.findProjectOwner(prj.getObject().getId());

        assertEquals(usr.getObject().getId(), ownr.getObject().getId());

        cleanData();
    }

    @Test
    public void findContributorsForProjectTest() throws Exception {
        logger.info("Find contributors for project test");

        var usr = createUser();
        var prj = createProject(true);

        contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), true);
        var contrs = contributorDao.findProjectContributors(prj.getObject().getId(), 0, 1);

        assertNull(contrs.getMessage());
        assertEquals(1, contrs.getObject().size());


        cleanData();
    }


    private ObjectWrapper<User> createUser() throws Exception {
        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        return userDao.createUser(login, password, email, firstName, lastName, roles);
    }

    private ObjectWrapper<Project> createProject(boolean isActive) {
        var name = "test";
        var startDate = new Date();

        var prj = projectDao.createProject(name, startDate);
        if (isActive) {
            prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);
        }

        return prj;
    }

    private void cleanData() {
        mapper.deleteContributors();
        mapper.deleteProjects();
        mapper.deleteUsers();
    }
}
