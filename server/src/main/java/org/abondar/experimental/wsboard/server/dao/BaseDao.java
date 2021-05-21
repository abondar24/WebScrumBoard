package org.abondar.experimental.wsboard.server.dao;

import org.abondar.experimental.wsboard.server.mapper.DataMapper;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Base data access object class
 *
 * @author a.bondar
 */
public class BaseDao {


    protected DataMapper mapper;

    protected PlatformTransactionManager transactionManager;


    public BaseDao(DataMapper mapper, PlatformTransactionManager transactionManager) {
        this.mapper = mapper;
        this.transactionManager = transactionManager;
    }


    public BaseDao(DataMapper mapper) {
        this.mapper = mapper;
    }

}
