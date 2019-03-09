package org.abondar.experimental.wsboard.base.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class DAO {

    @Autowired
    private DataMapper mapper;


}
