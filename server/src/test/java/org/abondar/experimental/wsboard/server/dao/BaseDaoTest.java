package org.abondar.experimental.wsboard.server.dao;

import net.sf.ehcache.transaction.local.JtaLocalTransactionStore;
import org.abondar.experimental.wsboard.server.config.TransactionConfig;
import org.abondar.experimental.wsboard.server.datamodel.Project;
import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.abondar.experimental.wsboard.server.datamodel.user.UserRole;
import org.abondar.experimental.wsboard.server.mapper.DataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import java.util.Date;


@ExtendWith({MockitoExtension.class})
@ActiveProfiles("test")
public class BaseDaoTest {


    @Mock
    protected DataMapper mapper;

    @InjectMocks
    protected ContributorDao contributorDao;

    @InjectMocks
    protected UserDao userDao;

    @InjectMocks
    protected ProjectDao projectDao;

    @InjectMocks
    protected SecurityCodeDao codeDao;

    @InjectMocks
    protected TaskDao taskDao;

    @InjectMocks
    protected SprintDao sprintDao;

    protected User usr;

    protected Project prj;

    @BeforeEach
    public void init() {
        usr = createUser();
        prj = createProject();


    }


    protected User createUser() {
        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name();

        return new User(login, password, email, firstName, lastName, roles);
    }

    protected User createUser(String login) throws Exception {
        var usr = createUser();
        if (!login.isBlank()) {
            usr.setLogin(login);
        }

        return usr;
    }


    protected Project createProject(boolean isActive) {

        var prj =  createProject();
        if (isActive) {
            prj.setActive(isActive);
        }

        return prj;
    }

    protected Project createProject()  {
        var name = "test";
        var startDate = new Date();

        return new Project(name,startDate);
    }

    protected void cleanData() {
        mapper.deleteCodes();
        mapper.deleteTasks();
        mapper.deleteSprints();
        mapper.deleteContributors();
        mapper.deleteProjects();
    }
}
