package org.abondar.experimental.wsboard.dao;

import org.abondar.experimental.wsboard.dao.data.DataMapper;

/**
 * Base data access object class
 *
 * @author a.bondar
 */
public class BaseDao {


    protected DataMapper mapper;

    public BaseDao(DataMapper mapper) {
        this.mapper = mapper;
    }

}
