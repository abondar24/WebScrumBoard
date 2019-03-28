package org.abondar.experimental.wsboard.base.config;

import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.dao.ContributorDao;
import org.abondar.experimental.wsboard.base.data.dao.ProjectDao;
import org.abondar.experimental.wsboard.base.data.dao.TaskDao;
import org.abondar.experimental.wsboard.base.data.dao.UserDao;
import org.abondar.experimental.wsboard.base.data.event.EventPublisher;
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
    public UserDao userDao(SqlSessionFactory sqlSessionFactory){
        return new UserDao(mapper(sqlSessionFactory),eventPublisher());
    }

    @Bean
    public ProjectDao projectDao(SqlSessionFactory sqlSessionFactory){
        return new ProjectDao(mapper(sqlSessionFactory));
    }

    @Bean
    public ContributorDao contributorDao(SqlSessionFactory sqlSessionFactory){
        return  new ContributorDao(mapper(sqlSessionFactory));
    }


    @Bean
    public TaskDao taskDao(SqlSessionFactory sqlSessionFactory){
        return  new TaskDao(mapper(sqlSessionFactory));
    }

    @Bean
    public EventPublisher eventPublisher(){
        return new EventPublisher();
    }

}
