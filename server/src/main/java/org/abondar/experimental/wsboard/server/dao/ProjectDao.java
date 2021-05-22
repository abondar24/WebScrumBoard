package org.abondar.experimental.wsboard.server.dao;

import org.abondar.experimental.wsboard.server.datamodel.Project;
import org.abondar.experimental.wsboard.server.exception.DataCreationException;
import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.abondar.experimental.wsboard.server.mapper.ContributorMapper;
import org.abondar.experimental.wsboard.server.mapper.TaskMapper;
import org.abondar.experimental.wsboard.server.mapper.ProjectMapper;
import org.abondar.experimental.wsboard.server.mapper.SprintMapper;
import org.abondar.experimental.wsboard.server.mapper.UserMapper;
import org.abondar.experimental.wsboard.server.util.LogMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Date;
import java.util.List;


/**
 * Data access object for project
 *
 * @author a.bondar
 */
@Component
public class ProjectDao{

    private static final Logger logger = LoggerFactory.getLogger(ProjectDao.class);

    private final UserMapper userMapper;

    private final ProjectMapper projectMapper;

    private final PlatformTransactionManager transactionManager;

    //TODO: replace with sprint and task mapper
    @Autowired
    private TaskMapper mapper;

    private final SprintMapper sprintMapper;

    private final ContributorMapper contributorMapper;

    @Autowired
    public ProjectDao(UserMapper userMapper,ProjectMapper projectMapper,
                      ContributorMapper contributorMapper,SprintMapper sprintMapper,PlatformTransactionManager transactionManager) {

       this.userMapper = userMapper;
       this.projectMapper = projectMapper;
       this.contributorMapper = contributorMapper;
       this.sprintMapper = sprintMapper;
       this.transactionManager = transactionManager;
    }


    /**
     * Create a new project
     *
     * @param name      - project name
     * @param startDate - project start date
     * @return project POJO
     * @throws DataExistenceException - project not found
     * @throws DataCreationException  - name or startDate are blank
     */
    public Project createProject(String name, Date startDate) throws DataExistenceException,
            DataCreationException {
        if (name == null || name.isBlank()) {
            logger.error(LogMessageUtil.BLANK_DATA);
            throw new DataCreationException(LogMessageUtil.BLANK_DATA);
        }

        checkProjectExists(name);

        if (startDate == null) {
            logger.error(LogMessageUtil.BLANK_DATA);
            throw new DataCreationException(LogMessageUtil.BLANK_DATA);
        }

        var prj = new Project(name, startDate);
        projectMapper.insertProject(prj);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Project successfully created ", prj.getId());
        logger.info(msg);
        return prj;
    }

    /**
     * Update an existing project
     *
     * @param id          - project id
     * @param name        - project name
     * @param repo        - project git repository
     * @param isActive    - project currently active
     * @param endDate     - project end date
     * @param description - project description
     * @return project POJO
     * @throws DataExistenceException - project not exists
     * @throws DataCreationException  - project activation or end date issue
     */
    public Project updateProject(long id, String name, String repo,
                                 Boolean isActive, Date endDate, String description)
            throws DataExistenceException, DataCreationException {

        var prj = findProjectById(id);

        if (name != null && !name.isBlank()) {
            checkProjectExists(name);
            prj.setName(name);
        }

        if (repo != null && !repo.isBlank()) {
            prj.setRepository(repo);
        }

        if (description != null && !description.isBlank()) {
            prj.setDescription(description);
        }

        TransactionStatus txStatus =
                transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            if (isActive != null) {
                if (!isActive) {
                    contributorMapper.deactivateProjectContributors(prj.getId());
                    if (endDate != null && !prj.getStartDate().after(endDate)) {
                        prj.setEndDate(endDate);
                    } else {
                        throw new DataCreationException(LogMessageUtil.WRONG_END_DATE);
                    }
                } else if (!prj.isActive()) {
                    throw new DataCreationException(LogMessageUtil.PROJECT_CANNOT_BE_REACTIVATED);
                }

                prj.setActive(isActive);

            }

            projectMapper.updateProject(prj);
        } catch (TransactionException ex) {
            transactionManager.rollback(txStatus);
            throw new DataExistenceException(ex.getMessage());
        }
        logger.info("Project successfully updated");

        return prj;
    }

    /**
     * Delete a project
     *
     * @param id - project id
     * @return project id
     * @throws DataExistenceException - project not found
     */
    public long deleteProject(long id) throws DataExistenceException {
        TransactionStatus txStatus =
                transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            findProjectById(id);

            mapper.deleteProjectTasks(id);
            sprintMapper.deleteProjectSprints(id);
            contributorMapper.deleteProjectContributors(id);
            projectMapper.deleteProject(id);

            var msg = String.format(LogMessageUtil.LOG_FORMAT + " %s", "Project ", id, " successfully updated");
            logger.info(msg);

            transactionManager.commit(txStatus);
            return id;
        } catch (DataExistenceException | TransactionException ex) {
            transactionManager.rollback(txStatus);
            throw new DataExistenceException(ex.getMessage());
        }

    }


    /**
     * Find a project by id
     *
     * @param id - project id
     * @return project pojo
     * @throws DataExistenceException - project not found
     */
    public Project findProjectById(long id) throws DataExistenceException {
        var prj = projectMapper.getProjectById(id);

        var msg = "";
        if (prj == null) {
            msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.PROJECT_NOT_EXISTS, id);
            logger.error(msg);
            throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);
        }

        msg = String.format(LogMessageUtil.LOG_FORMAT, "Project found ", prj.getId());
        logger.info(msg);
        return prj;
    }

    /**
     * Returns all projects where has contributed
     *
     * @param userId - user id
     * @return - list of projects related to user
     * @throws DataExistenceException - user not exists
     */
    public List<Project> findUserProjects(long userId) throws DataExistenceException {

        var msg = "";
        if (userMapper.getUserById(userId) == null) {
            msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.USER_NOT_EXISTS, userId);
            logger.error(msg);
            throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);
        }

        return projectMapper.getUserProjects(userId);
    }

    /**
     * Check project with name exists
     *
     * @param name - project name
     * @throws DataExistenceException - project not found
     */
    private void checkProjectExists(String name) throws DataExistenceException {
        var prj = projectMapper.getProjectByName(name);

        if (prj != null) {
            logger.error(LogMessageUtil.PROJECT_EXISTS);
            throw new DataExistenceException(LogMessageUtil.PROJECT_EXISTS);

        }
    }

}
