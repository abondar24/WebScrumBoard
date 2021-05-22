package org.abondar.experimental.wsboard.server.dao;

import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.abondar.experimental.wsboard.server.exception.DataCreationException;
import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.abondar.experimental.wsboard.server.mapper.ContributorMapper;
import org.abondar.experimental.wsboard.server.mapper.ProjectMapper;
import org.abondar.experimental.wsboard.server.mapper.UserMapper;
import org.abondar.experimental.wsboard.server.util.LogMessageUtil;

import org.abondar.experimental.wsboard.server.datamodel.Contributor;
import org.abondar.experimental.wsboard.server.datamodel.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


/**
 * Data access object for contributor
 *
 * @author a.bondar
 */
@Component
public class ContributorDao {

    private static final Logger logger = LoggerFactory.getLogger(ContributorDao.class);

    private UserMapper userMapper;

    private ProjectMapper projectMapper;

    private ContributorMapper contributorMapper;

    @Autowired
    public ContributorDao(UserMapper userMapper, ProjectMapper projectMapper, ContributorMapper contributorMapper) {
        this.userMapper = userMapper;
        this.projectMapper = projectMapper;
        this.contributorMapper = contributorMapper;
    }

    /**
     * Create a new contributor
     *
     * @param userId    - user id
     * @param projectId - project id to be assigned to
     * @param isOwner   - is contributor a project owner?
     * @return contributor POJO
     * @throws DataExistenceException - user or project does not exist
     * @throws DataCreationException  - project not active or has owner
     */
    public Contributor createContributor(long userId, long projectId, boolean isOwner)
            throws DataExistenceException, DataCreationException {

        var msg = "";
        checkUser(userId);


        var prj = findProjectById(projectId);

        if (!prj.isActive()) {
            logger.error(LogMessageUtil.PROJECT_NOT_ACTIVE);
            throw new DataCreationException(LogMessageUtil.PROJECT_NOT_ACTIVE);

        }

        if (isOwner) {
            checkUserIsOwner(projectId, userId);
        }


        var existingCtr = contributorMapper.getContributorByUserAndProject(userId,projectId);
        if (existingCtr!=null){
            if (existingCtr.isActive()){
                msg = String.format(LogMessageUtil.CONTRIBUTOR_EXISTS_LOG,projectId,userId);
                logger.error(msg);
                throw new DataExistenceException(LogMessageUtil.CONTRIBUTOR_EXISTS_EX);
            } else {
                existingCtr.setActive(true);
                if (isOwner){
                    existingCtr.setOwner(true);
                }

                contributorMapper.updateContributor(existingCtr);
                return existingCtr;
            }
        } else {
            var ctr = new Contributor(userId, projectId, isOwner);
            contributorMapper.insertContributor(ctr);
            msg = String.format(LogMessageUtil.LOG_FORMAT, "Contributor created ", ctr.getId());
            logger.info(msg);

            return ctr;
        }

    }

    /**
     * Update an existing contributor
     *
     * @param userId    - user id
     * @param projectId - project id
     * @param isOwner   - is contributor a project owner?
     * @param isActive  - is contributor currently working on a project?
     * @return contributor POJO
     * @throws DataExistenceException - contributor,user or project does not exist
     * @throws DataCreationException  - project has owner or doesn't have it at all
     */
    public Contributor updateContributor(long userId, long projectId, Boolean isOwner, Boolean isActive)
            throws DataExistenceException, DataCreationException {

        checkUser(userId);

        findProjectById(projectId);

        var msg = "";
        var ctr = contributorMapper.getContributorByUserAndProject(userId, projectId);
        if (ctr == null) {
            msg = String.format("%s with user id: %d", LogMessageUtil.CONTRIBUTOR_NOT_EXISTS, userId);
            logger.error(msg);
            throw new DataExistenceException(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS);

        }


        if (isOwner != null) {
            var ownr = userMapper.getProjectOwner(ctr.getProjectId());
            if (isOwner) {
                if (ctr.isOwner()) {
                    logger.error(LogMessageUtil.CONTRIBUTOR_IS_ALREADY_OWNER);
                    throw new DataCreationException(LogMessageUtil.CONTRIBUTOR_IS_ALREADY_OWNER);

                }

                if (!ctr.isActive()) {
                    msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.CONTRIBUTOR_NOT_ACTIVE, ctr.getId());
                    logger.error(msg);
                    throw new DataCreationException(LogMessageUtil.CONTRIBUTOR_NOT_ACTIVE);
                }

                if (ownr!=null){
                    var oldOwner = contributorMapper.getContributorByUserAndProject(ownr.getId(),projectId);
                    oldOwner.setOwner(false);
                    contributorMapper.updateContributor(oldOwner);
                }

            } else {

                if (ownr == null || ownr.getId() == ctr.getUserId()) {
                    logger.error(LogMessageUtil.PROJECT_HAS_NO_OWNER);
                    throw new DataCreationException(LogMessageUtil.PROJECT_HAS_NO_OWNER);

                }
            }

            ctr.setOwner(isOwner);
        }

        if (isActive != null) {
            if (ctr.isOwner() && !isActive) {
                logger.error(LogMessageUtil.CONTRIBUTOR_CANNOT_BE_DEACTIVATED);
                throw new DataCreationException(LogMessageUtil.CONTRIBUTOR_CANNOT_BE_DEACTIVATED);
            }
            ctr.setActive(isActive);
        }

        contributorMapper.updateContributor(ctr);

        msg = String.format(LogMessageUtil.LOG_FORMAT, "Contributor updated ", ctr.getId());
        logger.info(msg);

        return ctr;
    }

     public Optional<Long> findContributorByUserAndProject(long userId, long projectId) throws DataExistenceException{
          checkUser(userId);

          findProjectById(projectId);

         var ctrId = contributorMapper.getContributorByUserAndProject(userId,projectId).getId();

         var msg = String.format(LogMessageUtil.LOG_FORMAT, "Contributor found ", ctrId);
         logger.info(msg);

         return Optional.of(ctrId);
     }

    /**
     * Find a contributor who is a project owner
     *
     * @param projectId - project id
     * @return user POJO
     * @throws DataExistenceException - project does not exist
     * @throws DataCreationException  - project has no owner
     */
    public User findProjectOwner(long projectId) throws DataExistenceException, DataCreationException {

        findProjectById(projectId);

        var ownr = userMapper.getProjectOwner(projectId);
        if (ownr == null) {
            logger.error(LogMessageUtil.PROJECT_HAS_NO_OWNER);
            throw new DataCreationException(LogMessageUtil.PROJECT_HAS_NO_OWNER);
        }

        return ownr;
    }

    /**
     * Find the list of users who are project contributors with offset and limit
     *
     * @param projectId - project id
     * @param offset    - beginning of the list
     * @param limit     - end of the list
     * @return user POJO list
     * @throws DataExistenceException - project does not exist
     */
    public List<User> findProjectContributors(long projectId, int offset, int limit) throws DataExistenceException {

        findProjectById(projectId);

        var contrs = userMapper.getContributorsForProject(projectId, offset, limit);

        var  msg = String.format(LogMessageUtil.LOG_FORMAT, "Number contributors for project  ", contrs.size());
        logger.info(msg);

        return contrs;
    }


    /**
     * Find contributor by project and name
     * @param projectId - project id
     * @param login - contributor login
     * @return found contributor
     * @throws DataExistenceException - project or contributor not found
     */
    public Contributor findContributorByLogin(long projectId, String login) throws DataExistenceException {

        findProjectById(projectId);

        var ctr = contributorMapper.getContributorByLogin(projectId,login);
        if (ctr==null){
            throw new DataExistenceException(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS);
        }

        var msg = String.format(LogMessageUtil.LOG_FORMAT, "Found contributor", ctr.getId());
        logger.info(msg);

        return ctr;
    }

    /**
     * Counts the number of contributors per project
     * @param projectId - project to be counted
     * @return number of project contributors
     * @throws DataExistenceException - project not found
     */
    public Integer countProjectContributors(Long projectId) throws DataExistenceException{
        findProjectById(projectId);

        var res =  contributorMapper.countProjectContributors(projectId);
        var msg = String.format(LogMessageUtil.LOG_COUNT_FORMAT, "Counted contributors for project ", projectId,res);
        logger.info(msg);

        return res;

    }

    /**
     * Returns list of all contributors assigned to selected user
     *
     * @param userId - user id
     * @param offset - beginning of the list
     * @param limit  - end of the list
     * @return contributors list
     * @throws DataExistenceException - selected user doesn't exist
     */
    public List<Contributor> findContributorsByUserId(long userId, int offset, int limit)
            throws DataExistenceException {

       checkUser(userId);

        return contributorMapper.getContributorsByUserId(userId, offset, limit);
    }


    /**
     * Find a project by id
     *
     * @param projectId - project id
     * @return project pojo
     * @throws DataExistenceException
     */
    private Project findProjectById(long projectId) throws DataExistenceException {
        var prj = projectMapper.getProjectById(projectId);

        if (prj == null) {
            var msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.PROJECT_NOT_EXISTS, projectId);
            logger.error(msg);
            throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);
        }

        return prj;
    }


    /**
     * Check if user is already an owner for selected project
     *
     * @param projectId
     * @param userId
     * @throws DataCreationException
     */
    private void checkUserIsOwner(long projectId, long userId) throws DataCreationException {
        var owner = userMapper.getProjectOwner(projectId);
        if (owner != null && owner.getId() == userId) {
            logger.error(LogMessageUtil.CONTRIBUTOR_IS_ALREADY_OWNER);
            throw new DataCreationException(LogMessageUtil.CONTRIBUTOR_IS_ALREADY_OWNER);
        }
    }

    /**
     * Check if user exists
     * @param userId - user id
     * @throws DataExistenceException - user not found
     */
    private void checkUser(long userId) throws DataExistenceException {
        if (userMapper.getUserById(userId) == null) {
            var msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.USER_NOT_EXISTS, userId);
            logger.error(msg);
            throw new DataExistenceException(LogMessageUtil.USER_NOT_EXISTS);
        }
    }
}
