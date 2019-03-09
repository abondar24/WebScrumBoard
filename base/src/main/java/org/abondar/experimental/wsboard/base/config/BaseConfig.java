package org.abondar.experimental.wsboard.base.config;

import org.abondar.experimental.wsboard.base.dao.DAO;
import org.abondar.experimental.wsboard.base.dao.DataMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseConfig {
    @Bean
    public DataMapper mapper(SqlSessionFactory sqlSessionFactory){
        SqlSessionTemplate sessionTemplate = new SqlSessionTemplate( sqlSessionFactory );
        return sessionTemplate.getMapper(DataMapper.class);
    }

    @Bean
    public DAO dao(SqlSessionFactory sqlSessionFactory){
        return  new DAO(mapper(sqlSessionFactory));
    }

}
