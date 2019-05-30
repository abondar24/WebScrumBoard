package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;



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
     * @throws DataExistenceException - user or project does not exist
     * @throws DataCreationException - project not active or has owner
     * @return contributor POJO
     */
    public Contributor createContributor(long userId, long projectId, boolean isOwner)
            throws DataExistenceException, DataCreationException {

        var msg = "";
        if (mapper.getUserById(userId) == null) {
            msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.USER_NOT_EXISTS, userId);
            logger.error(msg);
            throw new DataExistenceException(LogMessageUtil.USER_NOT_EXISTS);
        }


        var prj = mapper.getProjectById(projectId);
        if (prj == null) {
            msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.PROJECT_NOT_EXISTS, projectId);
            logger.error(msg);
            throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);
        }

        if (!prj.isActive()) {
            logger.error(LogMessageUtil.PROJECT_NOT_ACTIVE);
            throw new DataCreationException(LogMessageUtil.PROJECT_NOT_ACTIVE);

        }

        if (isOwner) {
            var ownr = mapper.getProjectOwner(projectId);
            if (ownr != null && ownr.getId() == userId) {
                logger.error(LogMessageUtil.PROJECT_HAS_OWNER);
                throw new DataCreationException(LogMessageUtil.PROJECT_HAS_OWNER);

            }
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
     * @param contributorId - contributor id
     * @param isOwner       - is contributor a project owner?
     * @param isActive      - is contributor currently working on a project?
     * @throws DataExistenceException - contributor does not exist
     * @throws DataCreationException - project has owner or doesn't have it at all
     * @return contributor POJO
     */
    public Contributor updateContributor(long contributorId, Boolean isOwner, Boolean isActive)
    throws DataExistenceException,DataCreationException{

        var msg = "";
        var ctr = mapper.getContributorById(contributorId);
        if (ctr == null) {
            msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.CONTRIBUTOR_NOT_EXISTS, contributorId);
            logger.error(msg);
            throw new DataExistenceException(LogMessageUtil.CONTRIBUTOR_NOT_EXISTS);

        }

        if (isOwner != null) {
            if (isOwner) {
                if (ctr.isOwner()) {
                    logger.error(LogMessageUtil.PROJECT_HAS_OWNER);
                    throw new DataCreationException(LogMessageUtil.PROJECT_HAS_OWNER);

                }

                if (!ctr.isActive()) {
                    msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.CONTRIBUTOR_NOT_ACTIVE, ctr.getId());
                    logger.error(msg);
                    throw new DataCreationException(LogMessageUtil.CONTRIBUTOR_NOT_ACTIVE);
                }
            }


            if (!isOwner) {
                var ownr = mapper.getProjectOwner(ctr.getProjectId());
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


    /**
     * Find a contributor who is a project owner
     *
     * @param projectId - project id
     * @throws DataExistenceException - project does not exist
     * @throws DataCreationException - project has no owner
     * @return user POJO
     */
    public User findProjectOwner(long projectId) throws DataExistenceException,DataCreationException {

        var prj = mapper.getProjectById(projectId);
        if (prj == null) {
            var msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.PROJECT_NOT_EXISTS, projectId);
            logger.error(msg);
            throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);
        }

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
     * @throws DataExistenceException - project does not exist
     * @return user POJO list
     */
    public List<User>findProjectContributors(long projectId, int offset, int limit) throws DataExistenceException {


        var msg = "";
        var prj = mapper.getProjectById(projectId);
        if (prj == null) {
            msg = String.format(LogMessageUtil.LOG_FORMAT, LogMessageUtil.PROJECT_NOT_EXISTS, projectId);
            logger.error(msg);
            throw new DataExistenceException(LogMessageUtil.PROJECT_NOT_EXISTS);

        }

        var contrs = mapper.getContributorsForProject(projectId, offset, limit);

        msg = String.format(LogMessageUtil.LOG_FORMAT, "Number contributors for project  ", contrs.size());
        logger.info(msg);

        return contrs;
    }


}
