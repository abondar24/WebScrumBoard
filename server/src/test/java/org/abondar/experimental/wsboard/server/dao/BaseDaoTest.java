package org.abondar.experimental.wsboard.server.dao;

import org.abondar.experimental.wsboard.server.datamodel.Contributor;
import org.abondar.experimental.wsboard.server.datamodel.Project;
import org.abondar.experimental.wsboard.server.datamodel.Sprint;
import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.abondar.experimental.wsboard.server.datamodel.user.UserRole;
import org.abondar.experimental.wsboard.server.mapper.DataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

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

    protected Contributor ctr;

    protected Sprint sp;

    @BeforeEach
    public void init() {
        usr = createUser();
        prj = createProject();
        ctr = createContributor(usr.getId(), prj.getId());
        sp = createSprint(prj.getId());

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

        var prj = createProject();
        if (isActive) {
            prj.setActive(isActive);
        }

        return prj;
    }

    protected Project createProject() {
        var name = "test";
        var startDate = new Date();

        return new Project(name, startDate);
    }

    private Contributor createContributor(long usrId, long prjId) {
        return new Contributor(usrId, prjId, false);
    }

    private Sprint createSprint(long prjId) {
        return new Sprint("test", new Date(), new Date(), prjId);
    }


    protected void cleanData() {
        mapper.deleteTasks();
        mapper.deleteContributors();
    }
}
