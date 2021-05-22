package org.abondar.experimental.wsboard.server.dao;

import org.abondar.experimental.wsboard.server.datamodel.Project;
import org.abondar.experimental.wsboard.server.exception.DataCreationException;
import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


public class ProjectDaoTest extends DaoTest {

    @InjectMocks
    private ProjectDao projectDao;

    @Test
    public void createProjectTest() throws Exception {
        when(projectMapper.getProjectByName(prj.getName())).thenReturn(null);
        doNothing().when(projectMapper).insertProject(any(Project.class));

        var res = projectDao.createProject(prj.getName(), prj.getStartDate());
        assertEquals(0, prj.getId());

    }


    @Test
    public void createProjectExistsTest() {
        when(projectMapper.getProjectByName(prj.getName())).thenReturn(prj);
        assertThrows(DataExistenceException.class, () -> {
            projectDao.createProject(prj.getName(), prj.getStartDate());
        });
    }

    @Test
    public void createProjectBlankDataTest() {
        when(projectMapper.getProjectByName(prj.getName())).thenReturn(null);
        assertThrows(DataCreationException.class, () -> projectDao.createProject("", prj.getStartDate()));
        assertThrows(DataCreationException.class, () -> projectDao.createProject(prj.getName(), null));

    }

    @Test
    public void updateProjectTest() throws Exception {
        var id = prj.getId();
        projectDao = new ProjectDao(userMapper, projectMapper,contributorMapper,
                sprintMapper,taskMapper,new MockTransactionManager());

        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        when(projectMapper.getProjectByName(anyString())).thenReturn(null);
        doNothing().when(projectMapper).updateProject(any(Project.class));

        var res = projectDao.updateProject(prj.getId(), "newTest", "github.com/aaaa/aaa.git", true,
                null, "test");

        assertEquals(id, res.getId());

    }

    @Test
    public void updateProjectExistsTest() throws Exception {
        projectDao = new ProjectDao(userMapper, projectMapper,contributorMapper,sprintMapper,
                taskMapper,new MockTransactionManager());

        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        when(projectMapper.getProjectByName(anyString())).thenReturn(prj);

        assertThrows(DataExistenceException.class, () ->
                projectDao.updateProject(prj.getId(), prj.getName(), null, true, null, null));

    }

    @Test
    public void updateProjectInactiveTest() throws Exception {
        var id = prj.getId();
        projectDao = new ProjectDao(userMapper, projectMapper,contributorMapper,sprintMapper,
                taskMapper,new MockTransactionManager());

        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        when(projectMapper.getProjectByName(anyString())).thenReturn(null);
        doNothing().when(contributorMapper).deactivateProjectContributors(anyLong());
        doNothing().when(projectMapper).updateProject(any(Project.class));

        var res = projectDao.updateProject(prj.getId(), "newTest", "github.com/aaaa/aaa.git", false, new Date(), null);

        assertEquals(id, res.getId());

    }

    @Test
    public void updateProjectInactiveNullEndDateTest() {
        projectDao = new ProjectDao(userMapper, projectMapper,contributorMapper,sprintMapper,
                taskMapper,new MockTransactionManager());

        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        when(projectMapper.getProjectByName(anyString())).thenReturn(null);
        doNothing().when(contributorMapper).deactivateProjectContributors(anyLong());

        assertThrows(DataCreationException.class, () -> projectDao.updateProject(prj.getId(), "newTest",
                "github.com/aaaa/aaa.git", false, null, null));

    }

    @Test
    public void updateProjectReactivateTest() throws Exception {
        projectDao = new ProjectDao(userMapper, projectMapper,contributorMapper,
                sprintMapper,taskMapper,new MockTransactionManager());

        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        when(projectMapper.getProjectByName(anyString())).thenReturn(null);
        doNothing().when(contributorMapper).deactivateProjectContributors(anyLong());
        doNothing().when(projectMapper).updateProject(any(Project.class));

        var upd = projectDao.updateProject(prj.getId(), "newTest", "github.com/aaaa/aaa.git",
                false, new Date(), null);

        final long id = upd.getId();
        assertThrows(DataCreationException.class, () -> projectDao.updateProject(id, null,
                null, true, null, null));

    }

    @Test
    public void updateProjectInactiveWrongDateTest() {
        projectDao = new ProjectDao(userMapper, projectMapper,contributorMapper,sprintMapper,
                taskMapper,new MockTransactionManager());

        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        when(projectMapper.getProjectByName(anyString())).thenReturn(null);
        doNothing().when(contributorMapper).deactivateProjectContributors(anyLong());

        assertThrows(DataCreationException.class, () -> projectDao.updateProject(prj.getId(), "newTest",
                "github.com/aaaa/aaa.git", false, yesterday(), null));

    }


    @Test
    public void updateProjectNullTest() throws Exception {
        projectDao = new ProjectDao(userMapper, projectMapper,contributorMapper,sprintMapper,
                taskMapper,new MockTransactionManager());

        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        var res = projectDao.updateProject(prj.getId(), null, null, null, null, null);

        assertEquals(prj.getId(), res.getId());

    }


    @Test
    public void deleteProjectTest() throws Exception {
        projectDao = new ProjectDao(userMapper, projectMapper,contributorMapper,sprintMapper,taskMapper,new MockTransactionManager());

        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);
        doNothing().when(taskMapper).deleteProjectTasks(anyLong());
        doNothing().when(sprintMapper).deleteProjectSprints(anyLong());
        doNothing().when(contributorMapper).deleteProjectContributors(anyLong());
        doNothing().when(projectMapper).deleteProject(anyLong());

        var res = projectDao.deleteProject(prj.getId());

        assertEquals(prj.getId(), res);

    }

    @Test
    public void findProjectByIdTest() throws Exception {
        when(projectMapper.getProjectById(anyLong())).thenReturn(prj);

        var res = projectDao.findProjectById(prj.getId());
        assertEquals(prj.getName(), res.getName());
        assertEquals(prj.getStartDate(), res.getStartDate());

    }


    @Test
    public void findProjectNotFoundByIdTest() {
        when(projectMapper.getProjectById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class, () -> projectDao.findProjectById(100));

    }

    @Test
    public void findUserProjectsTest() throws Exception {
        when(userMapper.getUserById(anyLong())).thenReturn(usr);
        when(projectMapper.getUserProjects(anyLong())).thenReturn(List.of(prj));

        var projects = projectDao.findUserProjects(usr.getId());
        assertEquals(1, projects.size());

    }

    @Test
    public void findUserProjectsNotFoundTest() {
        when(userMapper.getUserById(anyLong())).thenReturn(null);
        assertThrows(DataExistenceException.class, () -> projectDao.findUserProjects(7));
    }


    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }


}
