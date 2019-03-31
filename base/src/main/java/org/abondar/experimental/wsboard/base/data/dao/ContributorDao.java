package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.base.data.ObjectWrapper;
import org.abondar.experimental.wsboard.base.data.event.EventPublisher;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.abondar.experimental.wsboard.datamodel.User;
import org.abondar.experimental.wsboard.datamodel.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ContributorDao extends BaseDao {

    private static Logger logger = LoggerFactory.getLogger(ContributorDao.class);


    public ContributorDao(DataMapper mapper, EventPublisher eventPublisher) {
        super(mapper, eventPublisher);
    }


    public ObjectWrapper<Contributor> createContributor(long userId, long projectId, boolean isOwner) {
        ObjectWrapper<Contributor> res = new ObjectWrapper<>();

        if (mapper.getUserById(userId) == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXIST + "with id: " + userId);
            res.setMessage(ErrorMessageUtil.USER_NOT_EXIST);
            return res;
        }


        var prj = mapper.getProjectById(projectId);
        if (prj == null) {
            logger.error(ErrorMessageUtil.PROJECT_NOT_EXISTS + "with id: " + projectId);
            res.setMessage(ErrorMessageUtil.PROJECT_NOT_EXISTS);

            return res;
        }

        if (!prj.isActive()) {
            logger.error(ErrorMessageUtil.PROJECT_NOT_ACTIVE);
            res.setMessage(ErrorMessageUtil.PROJECT_NOT_ACTIVE);

            return res;
        }

        if (isOwner) {
            var ownr = mapper.getProjectOwner(projectId);
            if (ownr != null && ownr.getId() == userId) {
                logger.error(ErrorMessageUtil.PROJECT_HAS_OWNER);
                res.setMessage(ErrorMessageUtil.PROJECT_HAS_OWNER);

                return res;
            }
        }


        var ctr = new Contributor(userId, projectId, isOwner);
        mapper.insertUpdateContributor(ctr);
        logger.info("Contributor created with id: " + ctr.getId());
        res.setObject(ctr);

        return res;
    }

    public ObjectWrapper<Contributor> updateContributorAsOwner(long contributorId, Boolean isOwner, Boolean isActive) {
        ObjectWrapper<Contributor> res = new ObjectWrapper<>();

        var ctr = mapper.getContributorById(contributorId);
        if (ctr == null) {
            logger.error(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS + "with id: " + contributorId);
            res.setMessage(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);
            return res;
        }

        if (isOwner != null) {
            if (ctr.isOwner()) {
                logger.error(ErrorMessageUtil.PROJECT_HAS_OWNER);
                res.setMessage(ErrorMessageUtil.PROJECT_HAS_OWNER);

                return res;
            }

            if (!isOwner) {
                var ownr = mapper.getProjectOwner(ctr.getProjectId());
                if (ownr == null || ownr.getId() == ctr.getUserId()) {
                    logger.error(ErrorMessageUtil.PROJECT_HAS_NO_OWNER);
                    res.setMessage(ErrorMessageUtil.PROJECT_HAS_NO_OWNER);

                    return res;
                }
            }

            ctr.setOwner(isOwner);
        }

        if (isActive != null) {
            ctr.setActive(isActive);
        }

        mapper.insertUpdateContributor(ctr);


        logger.info("Contributor updated with id: " + ctr.getId());
        res.setObject(ctr);

        return res;
    }


    public ObjectWrapper<User> findProjectOwner(long projectId) {
        ObjectWrapper<User> res = new ObjectWrapper<>();


        var prj = mapper.getProjectById(projectId);
        if (prj == null) {
            logger.error(ErrorMessageUtil.PROJECT_NOT_EXISTS + "with id: " + projectId);
            res.setMessage(ErrorMessageUtil.PROJECT_NOT_EXISTS);
            return res;
        }

        var ownr = mapper.getProjectOwner(projectId);
        if (ownr == null) {
            logger.error(ErrorMessageUtil.PROJECT_HAS_NO_OWNER);
            res.setMessage(ErrorMessageUtil.PROJECT_HAS_NO_OWNER);
            return res;
        }

        res.setObject(ownr);

        return res;
    }

    public ObjectWrapper<List<User>> findProjectContributors(long projectId, int offset, int limit) {
        ObjectWrapper<List<User>> res = new ObjectWrapper<>();


        var prj = mapper.getProjectById(projectId);
        if (prj == null) {
            logger.error(ErrorMessageUtil.PROJECT_NOT_EXISTS + "with id: " + projectId);
            res.setMessage(ErrorMessageUtil.PROJECT_NOT_EXISTS);
            return res;
        }

        var contrs = mapper.getContributorsForProject(projectId, offset, limit);

        logger.info("Number contributors for project with id: " + projectId + ": " + contrs.size());
        res.setObject(contrs);

        return res;
    }


}
