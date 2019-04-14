package org.abondar.experimental.wsboard.base.data.dao;

import org.abondar.experimental.wsboard.base.data.DataMapper;

public class BaseDao {


    protected DataMapper mapper;

    public BaseDao(DataMapper mapper) {
        this.mapper = mapper;
    }

}
