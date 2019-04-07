package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.ErrorMessageUtil;
import org.abondar.experimental.wsboard.base.data.ObjectWrapper;
import org.abondar.experimental.wsboard.base.data.event.EventPublisher;
import org.abondar.experimental.wsboard.datamodel.Sprint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class SprintDao extends BaseDao {

    private static Logger logger = LoggerFactory.getLogger(SprintDao.class);

    public SprintDao(DataMapper mapper, EventPublisher eventPublisher) {
        super(mapper, eventPublisher);
    }


    public ObjectWrapper<Sprint> createSprint(String name, Date startDate, Date endDate) {
        ObjectWrapper<Sprint> res = new ObjectWrapper<>();

        var sprint = mapper.getSprintByName(name);
        if (sprint != null) {
            logger.error(ErrorMessageUtil.SPRINT_EXISTS);
            res.setMessage(ErrorMessageUtil.SPRINT_EXISTS);

            return res;
        }

        sprint = new Sprint(name, startDate, endDate);

        mapper.insertUpdateSprint(sprint);
        logger.info("Created sprint with id: " + sprint.getId());

        res.setObject(sprint);
        return res;
    }


}
