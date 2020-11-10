package org.abondar.experimental.wsboard.project.dao;

import junit.framework.TestCase;

import org.abondar.experimental.wsboard.common.exception.DataCreationException;
import org.abondar.experimental.wsboard.common.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Sprint;
import org.abondar.experimental.wsboard.datamodel.task.Task;
import org.abondar.experimental.wsboard.project.data.Project;
import org.junit.jupiter.api.Assertions;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ProjectDaoTest  {

    @Autowired
    @Qualifier("projectDao")
    private ProjectDao projectDao;

    @Test
    public void createProjectTest() throws Exception {
        var prj = createProject();

        assertTrue(prj.getId() > 0);

    }


    @Test
    public void createProjectExistsTest() throws Exception {
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

        var prj = createProject();
        var id = prj.getId();
        prj = projectDao.updateProject(prj.getId(), "newTest", "github.com/aaaa/aaa.git", true,
                null, "test");

        assertEquals(id, prj.getId());

    }

    @Test
    public void updateProjectExistsTest() throws Exception {
        var prj = createProject();

        assertThrows(DataExistenceException.class, () ->
                projectDao.updateProject(prj.getId(), prj.getName(), null, true, null, null));

    }

    @Test
    public void updateProjectInactiveTest() throws Exception {
        var prj = createProject();
        var id = prj.getId();

        prj = projectDao.updateProject(prj.getId(), "newTest", "github.com/aaaa/aaa.git", false, new Date(), null);

        assertEquals(id, prj.getId());

    }

    @Test
    public void updateProjectInactiveNullEndDateTest() throws Exception {
        var prj = createProject();
        assertThrows(DataCreationException.class, () -> projectDao.updateProject(prj.getId(), "newTest",
                "github.com/aaaa/aaa.git", false, null, null));

    }

    @Test
    public void updateProjectReactivateTest() throws Exception {
        var prj = createProject();

        prj = projectDao.updateProject(prj.getId(), "newTest", "github.com/aaaa/aaa.git",
                false, new Date(), null);

        final long id = prj.getId();
        assertThrows(DataCreationException.class, () -> projectDao.updateProject(id, null,
                null, true, null, null));

    }

    @Test
    public void updateProjectInactiveWrongDateTest() throws Exception {
        var prj = createProject();

        assertThrows(DataCreationException.class, () -> projectDao.updateProject(prj.getId(), "newTest",
                "github.com/aaaa/aaa.git", false, yesterday(), null));

    }


    @Test
    public void updateProjectNullTest() throws Exception {
        var prj = createProject();
        var id = prj.getId();

        prj = projectDao.updateProject(prj.getId(), null, null, null, null, null);

        assertEquals(id, prj.getId());

    }


    @Test
    public void deleteProjectTest() throws Exception {
        var prj = createProject();
        var res = projectDao.deleteProject(prj.getId());

        assertEquals(prj.getId(), res);

    }

    @Test
    public void findProjectByIdTest() throws Exception {
        var prj = createProject();
        var res = projectDao.findProjectById(prj.getId());
        assertEquals(prj.getName(), res.getName());
        assertEquals(prj.getStartDate(), res.getStartDate());

    }


    @Test
    public void findProjectNotFoundByIdTest() {
        assertThrows(DataExistenceException.class, () -> projectDao.findProjectById(100));

    }

    @Test
    public void findUserProjectsTest() throws Exception {
        cleanData();

        var usr = createUser();
        var project = createProject();
        contributorDao.createContributor(usr.getId(), project.getId(), false);

        var projects = projectDao.findUserProjects(usr.getId());
        Assertions.assertEquals(1, projects.size());

        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();

    }

    @Test
    public void findUserProjectsNotFoundTest() {
        assertThrows(DataExistenceException.class, () -> projectDao.findUserProjects(7));
    }


    @Test
    public void deactivateContributorsByProjectTest() throws Exception {
        var usr = createUser();
        var prj = createProject();
        var ctr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        projectDao.updateProject(prj.getId(), "newTest", "github.com/aaaa/aaa.git", false, new Date(), null);

        var res = mapper.getContributorById(ctr.getId());
        Assertions.assertFalse(res.isActive());

    }


    @Test
    public void deleteAllByProjectTest() throws Exception {

        var usr = createUser();
        var prj = createProject();
        var contr = contributorDao.createContributor(usr.getId(), prj.getId(), false);

        var task = new Task(contr.getId(), new Date(), true, "name", "descr");
        mapper.insertTask(task);

        var sprint = new Sprint("test", new Date(), new Date(), prj.getId());
        mapper.insertSprint(sprint);

        projectDao.deleteProject(prj.getId());

        TestCase.assertNull(mapper.getTaskById(task.getId()));
        TestCase.assertNull(mapper.getSprintById(sprint.getId()));
        TestCase.assertNull(mapper.getContributorById(contr.getId()));
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


}
