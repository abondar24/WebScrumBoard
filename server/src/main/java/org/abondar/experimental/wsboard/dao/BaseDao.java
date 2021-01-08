package org.abondar.experimental.wsboard.base.dao;

import org.abondar.experimental.wsboard.base.mapper.DataMapper;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Base data access object class
 *
 * @author a.bondar
 */
public class BaseDao {


    protected DataMapper mapper;

    protected JtaTransactionManager transactionManager;


    public BaseDao(DataMapper mapper, JtaTransactionManager transactionManager) {
        this.mapper = mapper;
        this.transactionManager = transactionManager;
    }


    public BaseDao(DataMapper mapper) {
        this.mapper = mapper;
    }

}
