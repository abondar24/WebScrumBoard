package org.abondar.experimental.wsboard.server.dao;


import org.abondar.experimental.wsboard.server.config.TransactionConfig;
import org.abondar.experimental.wsboard.server.mapper.DataMapper;
import org.abondar.experimental.wsboard.server.datamodel.Project;
import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.abondar.experimental.wsboard.server.datamodel.user.UserRole;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import({ContributorDao.class,UserDao.class,ProjectDao.class})
public class BaseDaoTest {


    @Autowired
    protected DataMapper mapper;

    @Autowired
    protected ContributorDao contributorDao;

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected ProjectDao projectDao;


    protected User createUser() throws Exception {
        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name();

        return userDao.createUser(login, password, email, firstName, lastName, roles);
    }

    protected User createUser(String login) throws Exception {
        if (login.isBlank()){
           return createUser();
        }
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name();

        return userDao.createUser(login, password, email, firstName, lastName, roles);
    }

    protected Project createProject(boolean isActive) throws Exception {
        var name = "test";
        var startDate = new Date();

        var prj = projectDao.createProject(name, startDate);
        if (isActive) {
            prj = projectDao.updateProject(prj.getId(), null, null, true, null,null);
        }

        return prj;
    }

    protected Project createProject() throws Exception {
        var name = "test";
        var startDate = new Date();

        return projectDao.createProject(name, startDate);
    }

    protected void cleanData() {
        mapper.deleteCodes();
        mapper.deleteTasks();
        mapper.deleteSprints();
        mapper.deleteContributors();
        mapper.deleteUsers();
        mapper.deleteProjects();
    }
}
