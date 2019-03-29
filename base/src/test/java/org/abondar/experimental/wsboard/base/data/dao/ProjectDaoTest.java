package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.Main;
import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.ErrorMessageUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Main.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ProjectDaoTest {

    private static Logger logger = LoggerFactory.getLogger(ProjectDaoTest.class);

    @Autowired
    private DataMapper mapper;

    @Autowired
    @Qualifier("projectDao")
    private ProjectDao dao;



    @Test
    public void createProjectTest() {
        logger.info("Create project test");

        var name = "test";
        var startDate = new Date();

        var prj = dao.createProject(name, startDate);

        assertNull(prj.getMessage());
        assertTrue(prj.getObject().getId() > 0);

        mapper.deleteProjects();
    }


    @Test
    public void createProjectExistsTest() {
        logger.info("Create project test");

        var name = "test";
        var startDate = new Date();

        dao.createProject(name, startDate);
        var prj1 = dao.createProject(name, startDate);


        assertEquals(ErrorMessageUtil.PROJECT_EXISTS, prj1.getMessage());
        assertNull(prj1.getObject());

        mapper.deleteProjects();
    }


    @Test
    public void updateProjectTest() {
        logger.info("Update project test");

        var name = "test";
        var startDate = new Date();
        var prj = dao.createProject(name, startDate);

        prj = dao.updateProject(prj.getObject().getId(), "newTest", "github.com/aaaa/aaa.git", true, null);

        assertNull(prj.getMessage());
        mapper.deleteProjects();
    }

    @Test
    public void updateProjectInactiveTest() {
        logger.info("Update project test");

        var name = "test";
        var startDate = new Date();
        var prj = dao.createProject(name, startDate);

        prj = dao.updateProject(prj.getObject().getId(), "newTest", "github.com/aaaa/aaa.git", false, new Date());

        assertNull(prj.getMessage());
        mapper.deleteProjects();
    }

    @Test
    public void updateProjectInactiveNullTest() {
        logger.info("Update project inactive null end date test");

        var name = "test";
        var startDate = new Date();
        var prj = dao.createProject(name, startDate);

        prj = dao.updateProject(prj.getObject().getId(), "newTest", "github.com/aaaa/aaa.git", false, null);

        assertEquals(ErrorMessageUtil.WRONG_END_DATE, prj.getMessage());
        mapper.deleteProjects();
    }

    @Test
    public void updateProjectInactiveWrongDateTest() {
        logger.info("Update project inactive wrong end date test");

        var name = "test";
        var startDate = new Date();
        var prj = dao.createProject(name, startDate);

        prj = dao.updateProject(prj.getObject().getId(), "newTest", "github.com/aaaa/aaa.git", false, yesterday());

        assertEquals(ErrorMessageUtil.WRONG_END_DATE, prj.getMessage());
        mapper.deleteProjects();
    }


    @Test
    public void updateProjectNullTest() {
        logger.info("Update project null test");

        var name = "test";
        var startDate = new Date();
        var prj = dao.createProject(name, startDate);

        prj = dao.updateProject(prj.getObject().getId(), null, null, null, null);

        assertNull(prj.getMessage());
        mapper.deleteProjects();
    }


    @Test
    public void deleteProjectTest() {
        logger.info("Delete project test");

        var name = "test";
        var startDate = new Date();
        var prj = dao.createProject(name, startDate);

        var res = dao.deleteProject(prj.getObject().getId());

        assertNull(res.getMessage());
        assertEquals(prj.getObject().getId(), (long) res.getObject());

        mapper.deleteProjects();
    }

    @Test
    public void findProjectByIdTest() {
        logger.info("Find project by id test");

        var name = "test";
        var startDate = new Date();
        var prj = dao.createProject(name, startDate);

        var res = dao.findProjectById(prj.getObject().getId());
        assertEquals(prj.getObject().getName(), res.getObject().getName());
        assertEquals(prj.getObject().getStartDate(), res.getObject().getStartDate());

        mapper.deleteProjects();

    }


    @Test
    public void findProjectNotFoundByIdTest() {
        logger.info("Find project not found by id test");

        var prj = dao.findProjectById(100);

        assertEquals(ErrorMessageUtil.PROJECT_NOT_EXISTS, prj.getMessage());
        assertNull(prj.getObject());

        mapper.deleteProjects();

    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }


}
