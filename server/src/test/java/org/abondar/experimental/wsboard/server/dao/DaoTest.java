package org.abondar.experimental.wsboard.server.dao;

import org.abondar.experimental.wsboard.server.datamodel.Contributor;
import org.abondar.experimental.wsboard.server.datamodel.Project;
import org.abondar.experimental.wsboard.server.datamodel.Sprint;
import org.abondar.experimental.wsboard.server.datamodel.task.Task;
import org.abondar.experimental.wsboard.server.datamodel.task.TaskState;
import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.abondar.experimental.wsboard.server.datamodel.user.UserRole;
import org.abondar.experimental.wsboard.server.mapper.ContributorMapper;
import org.abondar.experimental.wsboard.server.mapper.DataMapper;
import org.abondar.experimental.wsboard.server.mapper.ProjectMapper;
import org.abondar.experimental.wsboard.server.mapper.SecurityCodeMapper;
import org.abondar.experimental.wsboard.server.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;


@ExtendWith({MockitoExtension.class})
@ActiveProfiles("test")
public class DaoTest {

    @Mock
    protected DataMapper mapper;

    @Mock
    protected UserMapper userMapper;

    @Mock
    protected ProjectMapper projectMapper;

    @Mock
    protected ContributorMapper contributorMapper;

    @Mock
    protected SecurityCodeMapper codeMapper;


    protected User usr;

    protected Project prj;

    protected Contributor ctr;

    protected Sprint sp;

    protected Task tsk;

    @BeforeEach
    public void init() {
        usr = createUser();
        prj = createProject();
        ctr = createContributor(usr.getId(), prj.getId());
        sp = createSprint(prj.getId());
        tsk = createTask(ctr.getId());
    }

    private User createUser() {
        var login = "login";
        var email = "email@email.com";
        var password = "pwd";
        var firstName = "fname";
        var lastName = "lname";
        var roles = UserRole.DEVELOPER.name() + ";" + UserRole.DEV_OPS.name();

        return new User(login, password, email, firstName, lastName, roles);
    }


    private Project createProject() {
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

    private Task createTask(long ctrId){
        var tsk = new Task(ctrId, new Date(), false, "name", "descr");
        tsk.setTaskState(TaskState.CREATED);
        return tsk;
    }


}
