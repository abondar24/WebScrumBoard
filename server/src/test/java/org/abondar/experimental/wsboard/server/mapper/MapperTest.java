package org.abondar.experimental.wsboard.server.mapper;

import org.abondar.experimental.wsboard.server.datamodel.Contributor;
import org.abondar.experimental.wsboard.server.datamodel.Project;
import org.abondar.experimental.wsboard.server.datamodel.Sprint;
import org.abondar.experimental.wsboard.server.datamodel.task.Task;
import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.abondar.experimental.wsboard.server.datamodel.user.UserRole;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;



@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MapperTest {

    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected ProjectMapper projectMapper;
    @Autowired
    protected ContributorMapper contributorMapper;
    @Autowired
    protected SecurityCodeMapper securityCodeMapper;

    @Autowired
    protected SprintMapper sprintMapper;

    @Autowired
    protected TaskMapper taskMapper;

    protected User createUser() {
        var roles = UserRole.DEVELOPER + ":" + UserRole.QA;
        var user = new User("testUser", "test@email.com",
                "test", "test", "12345", roles);

        userMapper.insertUser(user);

        return user;
    }

    protected Project createProject() {
        var project = new Project("test", new Date());
        projectMapper.insertProject(project);
        return project;
    }

    protected Contributor createContributor(long userId, long projectId, boolean isOwner) {
        var contributor = new Contributor(userId, projectId, isOwner);
        contributorMapper.insertContributor(contributor);
        return contributor;
    }

    protected Task createTask(long contributorId) {
        var task = new Task(contributorId, new Date(), true, "name", "descr");
        taskMapper.insertTask(task);
        return task;
    }

    protected Sprint createSprint(long projectId) {
        var sprint = new Sprint("test", new Date(), new Date(), projectId);
        sprintMapper.insertSprint(sprint);
        return sprint;
    }

    protected void cleanData() {
        taskMapper.deleteTasks();
        sprintMapper.deleteSprints();
        contributorMapper.deleteContributors();
        securityCodeMapper.deleteCodes();
        userMapper.deleteUsers();
        projectMapper.deleteProjects();
    }
}
