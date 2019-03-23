package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.base.data.ObjectWrapper;
import org.abondar.experimental.wsboard.datamodel.Contributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContributorDao {

    private static Logger logger = LoggerFactory.getLogger(ContributorDao.class);

    private DataMapper mapper;

    public ContributorDao(DataMapper mapper) {
        this.mapper = mapper;
    }



    public ObjectWrapper<Contributor> createContributor(long userId, long projectId, boolean isOwner) {
        ObjectWrapper<Contributor> res = new ObjectWrapper<>();

        var usr = mapper.getUserById(userId);
        if (usr == null) {
            logger.error(ErrorMessageUtil.USER_NOT_EXIST + "with id: " + userId);
            res.setMessage(ErrorMessageUtil.USER_NOT_EXIST);
            return res;
        }


        var prj = mapper.getProjectById(projectId);
        if (prj==null){
            logger.error(ErrorMessageUtil.PROJECT_NOT_EXISTS + "with id: " + projectId);
            res.setMessage(ErrorMessageUtil.PROJECT_NOT_EXISTS);

            return res;
        }

        if (!prj.isActive()){
            logger.error(ErrorMessageUtil.PROJECT_NOT_ACTIVE);
            res.setMessage(ErrorMessageUtil.PROJECT_NOT_ACTIVE);

            return res;
        }

        if (isOwner){
            var ownr = mapper.getProjectOwner(projectId);
            if (ownr!=null && ownr.getId()==userId){
                logger.error(ErrorMessageUtil.PROJECT_HAS_OWNER);
                res.setMessage(ErrorMessageUtil.PROJECT_HAS_OWNER);

                return res;
            }
        }


        var ctr = new Contributor(userId,projectId,isOwner);
        mapper.insertUpdateContributor(ctr);
        logger.info("Contributor created with id: " + ctr.getId());
        res.setObject(ctr);

        return res;
    }

    public ObjectWrapper<Contributor> setContributorAsOwner(long contributorId,boolean isOwner) {
        ObjectWrapper<Contributor> res = new ObjectWrapper<>();

        var ctr = mapper.getContributorById(contributorId);
        if (ctr == null) {
            logger.error(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS + "with id: " + contributorId);
            res.setMessage(ErrorMessageUtil.CONTRIBUTOR_NOT_EXISTS);
            return res;
        }

        if (ctr.isOwner()){
            logger.error(ErrorMessageUtil.PROJECT_HAS_OWNER);
            res.setMessage(ErrorMessageUtil.PROJECT_HAS_OWNER);

            return res;
        }

        if (!isOwner){
            var ownr = mapper.getProjectOwner(ctr.getProjectId());
            if (ownr==null || ownr.getId()==ctr.getUserId()){
                logger.error(ErrorMessageUtil.PROJECT_HAS_NO_OWNER);
                res.setMessage(ErrorMessageUtil.PROJECT_HAS_NO_OWNER);

                return res;
            }
        }

        ctr.setOwner(isOwner);

        mapper.insertUpdateContributor(ctr);
        logger.info("Contributor updated with id: " + ctr.getId());
        res.setObject(ctr);

        return res;
    }





}
