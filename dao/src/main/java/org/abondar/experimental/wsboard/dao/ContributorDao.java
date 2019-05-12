package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.dao.exception.DataCreationException;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.User;
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

        if (mapper.getUserById(userId) == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXISTS + "with id: " + userId);
            throw new DataExistenceException(ErrorMessageUtil.USER_NOT_EXISTS);
        }


        var prj = mapper.getProjectById(projectId);
        if (prj == null) {
            logger.error(ErrorMessageUtil.PROJECT_NOT_EXISTS + "with id: " + projectId);
            throw new DataExistenceException(ErrorMessageUtil.PROJECT_NOT_EXISTS);
        }

        if (!prj.isActive()) {
            logger.error(ErrorMessageUtil.PROJECT_NOT_ACTIVE);
            throw new DataCreationException(ErrorMessageUtil.PROJECT_NOT_ACTIVE);

        }

        if (isOwner) {
            var ownr = mapper.getProjectOwner(projectId);
            if (ownr != null && ownr.getId() == userId) {
                logger.error(ErrorMessageUtil.PROJECT_HAS_OWNER);
                throw new DataCreationException(ErrorMessageUtil.PROJECT_HAS_OWNER);

            }
        }


        var ctr = new Contributor(userId, projectId, isOwner);
        mapper.insertContributor(ctr);
        logger.info("Contributor created with id: " + ctr.getId());

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

        var ctr = mapper.getContributorById(contributorId);
        if (ctr == null) {
            logger.error(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS + "with id: " + contributorId);
            throw new DataExistenceException(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);

        }

        if (isOwner != null) {
            if (ctr.isOwner()) {
                logger.error(ErrorMessageUtil.PROJECT_HAS_OWNER);
                throw new DataCreationException(ErrorMessageUtil.PROJECT_HAS_OWNER);

            }

            if (!isOwner) {
                var ownr = mapper.getProjectOwner(ctr.getProjectId());
                if (ownr == null || ownr.getId() == ctr.getUserId()) {
                    logger.error(ErrorMessageUtil.PROJECT_HAS_NO_OWNER);
                    throw  new DataCreationException(ErrorMessageUtil.PROJECT_HAS_NO_OWNER);

                }
            }

            ctr.setOwner(isOwner);
        }

        if (isActive != null) {
            ctr.setActive(isActive);
        }

        mapper.updateContributor(ctr);

        logger.info("Contributor updated with id: " + ctr.getId());

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
            logger.error(ErrorMessageUtil.PROJECT_NOT_EXISTS + "with id: " + projectId);
            throw new DataExistenceException(ErrorMessageUtil.PROJECT_NOT_EXISTS);
        }

        var ownr = mapper.getProjectOwner(projectId);
        if (ownr == null) {
            logger.error(ErrorMessageUtil.PROJECT_HAS_NO_OWNER);
            throw new DataCreationException(ErrorMessageUtil.PROJECT_HAS_NO_OWNER);
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


        var prj = mapper.getProjectById(projectId);
        if (prj == null) {
            logger.error(ErrorMessageUtil.PROJECT_NOT_EXISTS + "with id: " + projectId);
            throw new DataExistenceException(ErrorMessageUtil.PROJECT_NOT_EXISTS);

        }

        var contrs = mapper.getContributorsForProject(projectId, offset, limit);

        logger.info("Number contributors for project with id: " + projectId + ": " + contrs.size());

        return contrs;
    }


}
