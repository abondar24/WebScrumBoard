package org.abondar.experimental.wsboard.project.mapper;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.project.data.Project;
import org.abondar.experimental.wsboard.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ProjectMapperTest {

    @Autowired
    private ProjectMapper mapper;


    @Test
    public void insertProjectTest() {
        mapper.deleteProjects();
        var project = createProject();
        assertTrue(project.getId() > 0);
    }

    @Test
    public void updateProjectTest() {
        mapper.deleteProjects();
        var project = createProject();
        var newName = "name1";
        project.setName(newName);

        mapper.updateProject(project);

        project = mapper.getProjectById(project.getId());

        assertEquals(newName, project.getName());

    }

    @Test
    public void getProjectByIdTest() {
        mapper.deleteProjects();
        var project = createProject();

        var res = mapper.getProjectById(project.getId());
        assertEquals(project.getId(), res.getId());
    }

    @Test
    public void getProjectByNameTest() {
        mapper.deleteProjects();
        var project = createProject();

        var res = mapper.getProjectByName(project.getName());
        assertEquals(project.getId(), res.getId());

    }



    private Project createProject() {
        var project = new Project("test", new Date());
        mapper.insertProject(project);
        return project;
    }


}
