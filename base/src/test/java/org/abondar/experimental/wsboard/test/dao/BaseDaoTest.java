package org.abondar.experimental.wsboard.test.dao;

import org.abondar.experimental.wsboard.base.WebScrumBoardApplication;
import org.abondar.experimental.wsboard.dao.ContributorDao;
import org.abondar.experimental.wsboard.dao.ProjectDao;
import org.abondar.experimental.wsboard.dao.UserDao;
import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = WebScrumBoardApplication.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BaseDaoTest {


    @Autowired
    protected DataMapper mapper;


    @Autowired
    @Qualifier("contributorDao")
    protected  ContributorDao contributorDao;

    @Autowired
    @Qualifier("userDao")
    protected  UserDao userDao;

    @Autowired
    @Qualifier("projectDao")
    protected  ProjectDao projectDao;



    protected void cleanData() {
        mapper.deleteCodes();
        mapper.deleteTasks();
        mapper.deleteSprints();
        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();
    }
}
