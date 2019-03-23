package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.base.data.MapperTest;
import org.abondar.experimental.wsboard.datamodel.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ContributorDaoTest {

    private static Logger logger = LoggerFactory.getLogger(MapperTest.class);

    @Autowired
    private DataMapper mapper;

    @Autowired
    private ContributorDao contributorDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProjectDao projectDao;

    @Test
    public void createContributorTest() throws Exception {
        logger.info("Create contributor test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), true);

        assertNull(contr.getMessage());
        assertTrue(contr.getObject().getId() > 0);

        mapper.deleteContributors();
        mapper.deleteProjects();
        mapper.deleteUsers();
    }


    @Test
    public void createContributorNotOwnerTest() throws Exception {
        logger.info("Create contributor not owner test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);

        assertNull(contr.getMessage());
        assertTrue(contr.getObject().getId() > 0);

        mapper.deleteContributors();
        mapper.deleteProjects();
        mapper.deleteUsers();
    }


    @Test
    public void createContributorActiveFalseTest() throws Exception {
        logger.info("Create contributor active false test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);

        assertEquals(ErrorMessageUtil.PROJECT_NOT_ACTIVE,contr.getMessage());
        assertNull(contr.getObject());

        mapper.deleteContributors();
        mapper.deleteProjects();
        mapper.deleteUsers();
    }

    @Test
    public void createContributorProjectHasOwnerTest() throws Exception {
        logger.info("Create contributor project has owner test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), true);
        contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), true);

        assertEquals(ErrorMessageUtil.PROJECT_HAS_OWNER,contr.getMessage());
        assertNull(contr.getObject());

        mapper.deleteContributors();
        mapper.deleteProjects();
        mapper.deleteUsers();
    }


    @Test
    public void setContributorAsOwnerTest() throws Exception {
        logger.info("Set contributor as owner test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        contr = contributorDao.setContributorAsOwner(contr.getObject().getId(), true);

        assertNull(contr.getMessage());

        mapper.deleteContributors();
        mapper.deleteProjects();
        mapper.deleteUsers();
    }


    @Test
    public void setContributorAsOwnerContributorNotExistsTest() {
        logger.info("Create contributor as owner contributor not exists");

        var contr = contributorDao.setContributorAsOwner(100, false);

        assertEquals(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS,contr.getMessage());
        assertNull(contr.getObject());

        mapper.deleteContributors();
        mapper.deleteProjects();
        mapper.deleteUsers();
    }

    @Test
    public void setContributorPrtojectHasOwnerTest() throws Exception {
        logger.info("Create contributor project has owner test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), true);
        contr = contributorDao.setContributorAsOwner(contr.getObject().getId(), true);

        assertEquals(ErrorMessageUtil.PROJECT_HAS_OWNER,contr.getMessage());
        assertNull(contr.getObject());

        mapper.deleteContributors();
        mapper.deleteProjects();
        mapper.deleteUsers();
    }

    @Test
    public void setContributorPrtojectHasNoOwnerTest() throws Exception {
        logger.info("Create contributor project has no owner test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        contr = contributorDao.setContributorAsOwner(contr.getObject().getId(), false);

        assertEquals(ErrorMessageUtil.PROJECT_HAS_NO_OWNER,contr.getMessage());
        assertNull(contr.getObject());

        mapper.deleteContributors();
        mapper.deleteProjects();
        mapper.deleteUsers();
    }

    @Test
    public void deleteContributorTest() throws Exception {
        logger.info("Create contributor project has no owner test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        var contr = contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), false);
        var res = contributorDao.deleteContributor(contr.getObject().getId());

        assertNull(contr.getMessage());
        assertEquals(contr.getObject().getId(), (long) res.getObject());

        mapper.deleteContributors();
        mapper.deleteProjects();
        mapper.deleteUsers();
    }

    @Test
    public void findProjectOwnerTest() throws Exception{
        logger.info("Find project owner test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), true);

        var ownr = contributorDao.findProjectOwner(prj.getObject().getId());

        assertEquals(usr.getObject().getId(),ownr.getObject().getId());

        mapper.deleteContributors();
        mapper.deleteProjects();
        mapper.deleteUsers();
    }

    @Test
    public void findContributorsForProjectTest() throws Exception {
        logger.info("Find contributors for project test");

        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = List.of(UserRole.Developer.name(), UserRole.DevOps.name());

        var usr = userDao.createUser(login, password, email, firstName, lastName, roles);

        var name = "test";
        var startDate = new Date();
        var prj = projectDao.createProject(name, startDate);
        prj = projectDao.updateProject(prj.getObject().getId(), null, null, true, null);

        contributorDao.createContributor(usr.getObject().getId(), prj.getObject().getId(), true);
        var contrs = contributorDao.findProjectContributors(prj.getObject().getId(),0,1);

        assertNull(contrs.getMessage());
        assertEquals(1,contrs.getObject().size());


        mapper.deleteContributors();
        mapper.deleteProjects();
        mapper.deleteUsers();
    }
}
