package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.event.EventPublisher;

public class BaseDao {

    protected EventPublisher eventPublisher;

    protected DataMapper mapper;


    public BaseDao(DataMapper mapper, EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.mapper = mapper;
    }

}
