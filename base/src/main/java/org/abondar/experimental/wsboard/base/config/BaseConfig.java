package org.abondar.experimental.wsboard.base.config;

import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.dao.*;
import org.abondar.experimental.wsboard.base.data.event.EventPublisher;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseConfig {

    @Bean
    public DataMapper mapper(SqlSessionFactory sqlSessionFactory) {
        SqlSessionTemplate sessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        return sessionTemplate.getMapper(DataMapper.class);
    }

    @Bean(name = "userDao")
    public BaseDao userDao(SqlSessionFactory sqlSessionFactory) {
        return new UserDao(mapper(sqlSessionFactory), eventPublisher());
    }

    @Bean(name = "projectDao")
    public BaseDao projectDao(SqlSessionFactory sqlSessionFactory) {
        return new ProjectDao(mapper(sqlSessionFactory), eventPublisher());
    }

    @Bean(name="contributorDao")
    public BaseDao contributorDao(SqlSessionFactory sqlSessionFactory) {
        return new ContributorDao(mapper(sqlSessionFactory), eventPublisher());
    }


    @Bean(name="taskDao")
    public BaseDao taskDao(SqlSessionFactory sqlSessionFactory) {
        return new TaskDao(mapper(sqlSessionFactory), eventPublisher());
    }

    @Bean(name = "sprintDao")
    public BaseDao sprintDao(SqlSessionFactory sqlSessionFactory) {
        return new SprintDao(mapper(sqlSessionFactory), eventPublisher());
    }

    @Bean
    public EventPublisher eventPublisher() {
        return new EventPublisher();
    }

}
