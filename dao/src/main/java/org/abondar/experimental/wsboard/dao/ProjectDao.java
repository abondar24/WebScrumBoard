package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Date;
import java.util.List;


/**
 * Data access object for project
 *
 * @author a.bondar
 */
public class ProjectDao extends BaseDao {

    private static Logger logger = LoggerFactory.getLogger(ProjectDao.class);

    private JtaTransactionManager transactionManager;

    public ProjectDao(DataMapper mapper,JtaTransactionManager transactionManager) {
        super(mapper);
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
        mapper.insertProject(prj);

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Project successfully created ", prj.getId());
        logger.info(msg);
        return prj;
    }

    /**
     * Update an existing project
     *
     * @param id       - project id
     * @param name     - project name
     * @param repo     - project git repository
     * @param isActive - project currently active
     * @param endDate  - project end date
     * @param description - project description
     * @return project POJO
     * @throws DataExistenceException - project not exists
     * @throws DataCreationException - project activation or end date issue
     */
    public Project updateProject(long id, String name, String repo,
                                 Boolean isActive, Date endDate,String description)
            throws DataExistenceException, DataCreationException {

        var prj = findProjectById(id);

        if (name != null && !name.isBlank()) {
            checkProjectExists(name);
            prj.setName(name);
        }

        if (repo != null && !repo.isBlank()) {
            prj.setName(name);
        }

        if (description != null && !description.isBlank()) {
            prj.setDescription(description);
        }

        TransactionStatus txStatus =
                transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
        if (isActive != null) {
            if (isActive && !prj.isActive()) {
                throw new DataCreationException(LogMessageUtil.PROJECT_CANNOT_BE_REACTIVATED);
            } else if (!isActive) {
                mapper.deactivateProjectContributors(prj.getId());
                if (endDate != null && !prj.getStartDate().after(endDate)) {
                    prj.setEndDate(endDate);
                } else {
                    throw new DataCreationException(LogMessageUtil.WRONG_END_DATE);
                }
            }

            prj.setActive(isActive);

        }

        mapper.updateProject(prj);
        } catch (TransactionException ex){
            transactionManager.rollback(txStatus);
            throw  new DataExistenceException(ex.getMessage());
        }
        logger.info("Project successfully updated");

        return prj;
    }

    /**
     * Delete a project
     *
     * @param id - project id
     * @return project id
     * @throws DataExistenceException
     */
    public long deleteProject(long id) throws DataExistenceException {
        TransactionStatus txStatus =
                transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            findProjectById(id);

            mapper.deleteProjectTasks(id);
            mapper.deleteProjectSprints(id);
            mapper.deleteProjectContributors(id);
            mapper.deleteProject(id);

            var msg = String.format(LogMessageUtil.LOG_FORMAT + " %s", "Project ", id, " successfully updated");
            logger.info(msg);

            transactionManager.commit(txStatus);
            return id;
        } catch (DataExistenceException | TransactionException ex){
            transactionManager.rollback(txStatus);
            throw  new DataExistenceException(ex.getMessage());
        }

    }


    /**
     * Find a project by id
     *
     * @param id - project id
     * @return project pojo
     * @throws DataExistenceException
     */
    public Project findProjectById(long id) throws DataExistenceException {
        var prj = mapper.getProjectById(id);

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
     * @param userId - user id
     * @return - list of projects related to user
     * @throws DataExistenceException - user not exists
     */
    public List<Project> findUserProjects(long userId) throws DataExistenceException{

        var msg = "";
        if (mapper.getUserById(userId) == null){
            msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.USER_NOT_EXISTS, userId);
            logger.error(msg);
            throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);
        }

        return mapper.getUserProjects(userId);
    }

    /**
     * Check project with name exists
     *
     * @param name - project name
     * @throws DataExistenceException
     */
    private void checkProjectExists(String name) throws DataExistenceException {
        var prj = mapper.getProjectByName(name);

        if (prj != null) {
            logger.error(LogMessageUtil.PROJECT_EXISTS);
            throw new DataExistenceException(LogMessageUtil.PROJECT_EXISTS);

        }
    }

}
