package org.abondar.experimental.wsboard.test.dao;

import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.Sprint;
import org.abondar.experimental.wsboard.datamodel.task.Task;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.datamodel.user.UserRole;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.TestCase.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ProjectDaoTest extends BaseDaoTest {


    @Test
    public void createProjectTest() throws Exception {
        cleanData();
        var prj = createProject();

        assertTrue(prj.getId() > 0);

    }


    @Test
    public void createProjectExistsTest() throws Exception {
        cleanData();
        createProject();

        assertThrows(DataExistenceException.class, this::createProject);
    }

    @Test
    public void createProjectBlankDataTest() {
        cleanData();
        assertThrows(DataCreationException.class, () -> projectDao.createProject("", new Date()));
        assertThrows(DataCreationException.class, () -> projectDao.createProject("test", null));

    }

    @Test
    public void updateProjectTest() throws Exception {
        cleanData();
        var prj = createProject();
        var id = prj.getId();
        prj = projectDao.updateProject(prj.getId(), "newTest", "github.com/aaaa/aaa.git", true,
                null, "test");

        assertEquals(id, prj.getId());

    }

    @Test
    public void updateProjectExistsTest() throws Exception {
        cleanData();
        var prj = createProject();

        assertThrows(DataExistenceException.class, () ->
                projectDao.updateProject(prj.getId(), prj.getName(), null, true, null, null));

    }

    @Test
    public void updateProjectInactiveTest() throws Exception {
        cleanData();
        var prj = createProject();
        var id = prj.getId();

        prj = projectDao.updateProject(prj.getId(), "newTest", "github.com/aaaa/aaa.git", false, new Date(), null);

        assertEquals(id, prj.getId());

    }

    @Test
    public void updateProjectInactiveNullEndDateTest() throws Exception {
        cleanData();
        var prj = createProject();
        assertThrows(DataCreationException.class, () -> projectDao.updateProject(prj.getId(), "newTest",
                "github.com/aaaa/aaa.git", false, null, null));

    }

    @Test
    public void updateProjectReactivateTest() throws Exception {
        cleanData();
        var prj = createProject();

        prj = projectDao.updateProject(prj.getId(), "newTest", "github.com/aaaa/aaa.git",
                false, new Date(), null);

        final long id = prj.getId();
        assertThrows(DataCreationException.class, () -> projectDao.updateProject(id, null,
                null, true, null, null));

    }

    @Test
    public void updateProjectInactiveWrongDateTest() throws Exception {
        cleanData();
        var prj = createProject();

        assertThrows(DataCreationException.class, () -> projectDao.updateProject(prj.getId(), "newTest",
                "github.com/aaaa/aaa.git", false, yesterday(), null));

    }


    @Test
    public void updateProjectNullTest() throws Exception {
        cleanData();
        var prj = createProject();
        var id = prj.getId();

        prj = projectDao.updateProject(prj.getId(), null, null, null, null, null);

        assertEquals(id, prj.getId());

    }


    @Test
    public void deleteProjectTest() throws Exception {
        cleanData();
        var prj = createProject();
        var res = projectDao.deleteProject(prj.getId());

        assertEquals(prj.getId(), res);

    }

    @Test
    public void findProjectByIdTest() throws Exception {
        cleanData();
        var prj = createProject();
        var res = projectDao.findProjectById(prj.getId());
        assertEquals(prj.getName(), res.getName());
        assertEquals(prj.getStartDate(), res.getStartDate());

    }


    @Test
    public void findProjectNotFoundByIdTest() {
        cleanData();
        assertThrows(DataExistenceException.class, () -> projectDao.findProjectById(100));

    }

    @Test
    public void findUserProjectsTest() throws Exception {
        cleanData();

        var usr = createUser();
        var project = createProject();
        contributorDao.createContributor(usr.getId(), project.getId(), false);

        var projects = projectDao.findUserProjects(usr.getId());
        assertEquals(1, projects.size());

        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();

    }

    @Test
    public void findUserProjectsNotFoundTest() {
        cleanData();
        assertThrows(DataExistenceException.class, () -> projectDao.findUserProjects(7));
    }


    @Test
    public void deactivateContributorsByProjectTest() throws Exception {
        cleanData();
        var usr = createUser();
        var prj = createProject();
        var ctr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        projectDao.updateProject(prj.getId(), "newTest", "github.com/aaaa/aaa.git", false, new Date(), null);

        var res = mapper.getContributorById(ctr.getId());
        assertFalse(res.isActive());

    }


    @Test
    public void deleteAllByProjectTest() throws Exception {
        cleanData();

        var usr = createUser();
        var prj = createProject();
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = new Task(contr.getId(), new Date(), true, "name", "descr");
        mapper.insertTask(task);

        var sprint = new Sprint("test", new Date(), new Date(), prj.getId());
        mapper.insertSprint(sprint);

        projectDao.deleteProject(prj.getId());

        assertNull(mapper.getTaskById(task.getId()));
        assertNull(mapper.getSprintById(sprint.getId()));
        assertNull(mapper.getContributorById(contr.getId()));
        assertNull(mapper.getProjectById(prj.getId()));

    }


    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }


    private Project createProject() throws Exception {
        var name = "test";
        var startDate = new Date();

        return projectDao.createProject(name, startDate);
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
