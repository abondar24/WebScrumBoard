package org.abondar.experimental.wsboard.test.dao;

import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.dao.ContributorDao;
import org.abondar.experimental.wsboard.dao.ProjectDao;
import org.abondar.experimental.wsboard.dao.UserDao;
import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.Sprint;
import org.abondar.experimental.wsboard.datamodel.task.Task;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.TestCase.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(classes = WebScrumBoardApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ProjectDaoTest {


    @Autowired
    private DataMapper mapper;

    @Autowired
    @Qualifier("projectDao")
    private ProjectDao dao;

    @Autowired
    @Qualifier("userDao")
    private UserDao userDao;

    @Autowired
    @Qualifier("contributorDao")
    private ContributorDao contributorDao;



    @Test
    public void createProjectTest() throws Exception {
        var prj = createProject();

        assertTrue(prj.getId() > 0);

        mapper.deleteProjects();
    }


    @Test
    public void createProjectExistsTest() throws Exception {
        createProject();

        assertThrows(DataExistenceException.class, this::createProject);
        mapper.deleteProjects();
    }

    @Test
    public void createProjectBlankDataTest() {
        assertThrows(DataCreationException.class, () -> dao.createProject("", new Date()));
        assertThrows(DataCreationException.class, () -> dao.createProject("test", null));

        mapper.deleteProjects();
    }

    @Test
    public void updateProjectTest() throws Exception {
        var prj = createProject();
        var id = prj.getId();
        prj = dao.updateProject(prj.getId(), "newTest", "github.com/aaaa/aaa.git", true,
                null,"test");

        assertEquals(id, prj.getId());

        mapper.deleteProjects();
    }

    @Test
    public void updateProjectExistsTest() throws Exception {
        var prj = createProject();


        assertThrows(DataExistenceException.class, () ->
                dao.updateProject(prj.getId(), prj.getName(), null, true, null,null));

        mapper.deleteProjects();
    }

    @Test
    public void updateProjectInactiveTest() throws Exception {
        var prj = createProject();
        var id = prj.getId();

        prj = dao.updateProject(prj.getId(), "newTest", "github.com/aaaa/aaa.git", false, new Date(),null);

        assertEquals(id, prj.getId());

        mapper.deleteProjects();
    }

    @Test
    public void updateProjectInactiveNullEndDateTest() throws Exception {
        var prj = createProject();
        assertThrows(DataCreationException.class, () -> dao.updateProject(prj.getId(), "newTest",
                "github.com/aaaa/aaa.git", false, null,null));

        mapper.deleteProjects();
    }

    @Test
    public void updateProjectReactivateTest() throws Exception {
        var prj = createProject();

        prj = dao.updateProject(prj.getId(), "newTest", "github.com/aaaa/aaa.git",
                false, new Date(),null);

        final long id = prj.getId();
        assertThrows(DataCreationException.class, () -> dao.updateProject(id, null,
                null, true, null,null));

        mapper.deleteProjects();
    }

    @Test
    public void updateProjectInactiveWrongDateTest() throws Exception {
        var prj = createProject();

        assertThrows(DataCreationException.class, () -> dao.updateProject(prj.getId(), "newTest",
                "github.com/aaaa/aaa.git", false, yesterday(),null));
        mapper.deleteProjects();
    }


    @Test
    public void updateProjectNullTest() throws Exception {
        var prj = createProject();
        var id = prj.getId();

        prj = dao.updateProject(prj.getId(), null, null, null, null,null);

        assertEquals(id, prj.getId());
        mapper.deleteProjects();
    }


    @Test
    public void deleteProjectTest() throws Exception {
        var prj = createProject();
        var res = dao.deleteProject(prj.getId());

        assertEquals(prj.getId(), res);

        mapper.deleteProjects();
    }

    @Test
    public void findProjectByIdTest() throws Exception {
        var prj = createProject();
        var res = dao.findProjectById(prj.getId());
        assertEquals(prj.getName(), res.getName());
        assertEquals(prj.getStartDate(), res.getStartDate());

        mapper.deleteProjects();

    }


    @Test
    public void findProjectNotFoundByIdTest() {
        assertThrows(DataExistenceException.class, () -> dao.findProjectById(100));

    }

    @Test
    public void findUserProjectsTest() throws Exception{

        var usr = createUser();
        var project = createProject();
        contributorDao.createContributor(usr.getId(), project.getId(), false);

        var projects = dao.findUserProjects(usr.getId());
        assertEquals(1,projects.size());

        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();

    }

    @Test
    public void findUserProjectsNotFoundTest() {
           assertThrows(DataExistenceException.class,()-> dao.findUserProjects(7));
    }



    @Test
    public void deactivateContributorsByProjectTest() throws Exception {
        var usr = createUser();
        var prj = createProject();
        var ctr = contributorDao.createContributor(usr.getId(),prj.getId(),false);

        dao.updateProject(prj.getId(), "newTest", "github.com/aaaa/aaa.git", false, new Date(),null);

        var res = mapper.getContributorById(ctr.getId());
        assertFalse(res.isActive());

        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();

    }


    @Test
    public void deleteAllByProjectTest() throws Exception{
        var usr = createUser();
        var prj = createProject();
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = new Task(contr.getId(), new Date(), true, "name", "descr");
        mapper.insertTask(task);

        var sprint = new Sprint("test", new Date(), new Date(),prj.getId());
        mapper.insertSprint(sprint);

        dao.deleteProject(prj.getId());

        assertNull(mapper.getTaskById(task.getId()));
        assertNull(mapper.getSprintById(sprint.getId()));
        assertNull(mapper.getContributorById(contr.getId()));
        assertNull(mapper.getProjectById(prj.getId()));

        mapper.deleteUsers();
    }


    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }


    private Project createProject() throws Exception {
        var name = "test";
        var startDate = new Date();

        return dao.createProject(name, startDate);
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

}
