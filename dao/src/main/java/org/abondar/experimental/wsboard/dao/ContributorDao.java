package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.Project;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;


/**
 * Data access object for contributor
 *
 * @author a.bondar
 */
public class ContributorDao extends BaseDao {

    private static Logger logger = LoggerFactory.getLogger(ContributorDao.class);


    public ContributorDao(DataMapper mapper) {
        super(mapper);
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

        //TODO: reactivate contributor if previously it has been created
        var msg = "";
        if (mapper.getUserById(userId) == null) {
            msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.USER_NOT_EXISTS, userId);
            logger.error(msg);
            throw new DataExistenceException(LogMessageUtil.USER_NOT_EXISTS);
        }


        var prj = findProjectById(projectId);

        if (!prj.isActive()) {
            logger.error(LogMessageUtil.PROJECT_NOT_ACTIVE);
            throw new DataCreationException(LogMessageUtil.PROJECT_NOT_ACTIVE);

        }

        if (isOwner) {
            checkUserIsOwner(projectId, userId);
        }


        var ctr = new Contributor(userId, projectId, isOwner);
        mapper.insertContributor(ctr);
        msg = String.format(LogMessageUtil.LOG_FORMAT, "Contributor created ", ctr.getId());
        logger.info(msg);

        return ctr;
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

        if (mapper.getUserById(userId)==null){
            throw new DataExistenceException(LogMessageUtil.USER_NOT_EXISTS);
        }

        if (mapper.getProjectById(projectId)==null){
            throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);
        }

        var msg = "";
        var ctr = mapper.getContributorByUserAndProject(userId, projectId);
        if (ctr == null) {
            msg = String.format("%s with user id: %d", LogMessageUtil.CONTRIBUTOR_NOT_EXISTS, userId);
            logger.error(msg);
            throw new DataExistenceException(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS);

        }

        if (isOwner != null) {
            var ownr = mapper.getProjectOwner(ctr.getProjectId());
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
                    var oldOwner = mapper.getContributorByUserAndProject(ownr.getId(),projectId);
                    oldOwner.setOwner(false);
                    mapper.updateContributor(oldOwner);
                }

            }


            if (!isOwner) {

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

        mapper.updateContributor(ctr);

        msg = String.format(LogMessageUtil.LOG_FORMAT, "Contributor updated ", ctr.getId());
        logger.info(msg);

        return ctr;
    }

     public Optional<Long> findContributorByUserAndProject(long userId, long projectId) throws DataExistenceException{
         if (mapper.getUserById(userId)==null){
             throw new DataExistenceException(LogMessageUtil.USER_NOT_EXISTS);
         }

         if (mapper.getProjectById(projectId)==null){
             throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);
         }

         var ctrId = mapper.getContributorByUserAndProject(userId,projectId).getId();

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

        var ownr = mapper.getProjectOwner(projectId);
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


        var msg = "";
        findProjectById(projectId);

        var contrs = mapper.getContributorsForProject(projectId, offset, limit);

        msg = String.format(LogMessageUtil.LOG_FORMAT, "Number contributors for project  ", contrs.size());
        logger.info(msg);

        return contrs;
    }

    /**
     * Counts the number of contributors per project
     * @param projectId - project to be counted
     * @return number of project contributors
     * @throws DataExistenceException - project not found
     */
    public Integer countProjectContributors(Long projectId) throws DataExistenceException{
        findProjectById(projectId);

        var res =  mapper.countProjectContributors(projectId);
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

        if (mapper.getUserById(userId) == null) {
            throw new DataExistenceException(LogMessageUtil.USER_NOT_EXISTS);
        }

        return mapper.getContributorsByUserId(userId, offset, limit);
    }


    /**
     * Find a project by id
     *
     * @param projectId - project id
     * @return project pojo
     * @throws DataExistenceException
     */
    private Project findProjectById(long projectId) throws DataExistenceException {
        var prj = mapper.getProjectById(projectId);

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
        var owner = mapper.getProjectOwner(projectId);
        if (owner != null && owner.getId() == userId) {
            logger.error(LogMessageUtil.CONTRIBUTOR_IS_ALREADY_OWNER);
            throw new DataCreationException(LogMessageUtil.CONTRIBUTOR_IS_ALREADY_OWNER);
        }
    }
}
