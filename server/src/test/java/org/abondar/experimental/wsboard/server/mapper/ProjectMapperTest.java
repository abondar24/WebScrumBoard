package org.abondar.experimental.wsboard.server.mapper;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class ProjectMapperTest extends MapperTest{


    @Test
    public void updateProjectTest() {
        cleanData();
        var project = createProject();
        var newName = "name1";
        project.setName(newName);

        projectMapper.updateProject(project);

        project = projectMapper.getProjectById(project.getId());

        assertEquals(newName, project.getName());

    }

    @Test
    public void getProjectByIdTest() {
        cleanData();
        var project = createProject();

        var res = projectMapper.getProjectById(project.getId());
        assertEquals(project.getId(), res.getId());
    }

    @Test
    public void getProjectByNameTest() {
        cleanData();
        var project = createProject();

        var res = projectMapper.getProjectByName(project.getName());
        assertEquals(project.getId(), res.getId());

    }

    @Test
    public void getUserProjectTest() {
        cleanData();
        var user = createUser();
        var project = createProject();
        var project1 = createProject();
        createContributor(user.getId(), project.getId(), true);
        createContributor(user.getId(), project1.getId(), true);

        var res = projectMapper.getUserProjects(user.getId());
        assertEquals(2, res.size());

    }

    @Test
    public void deleteProjectTest() {
        cleanData();
        var project = createProject();

        projectMapper.deleteProject(project.getId());

        var res = projectMapper.getProjectById(project.getId());
        assertNull(res);
    }
}
