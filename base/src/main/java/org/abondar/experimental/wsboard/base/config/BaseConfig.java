package org.abondar.experimental.wsboard.base.config;

import org.abondar.experimental.wsboard.base.data.DataMapper;
import org.abondar.experimental.wsboard.base.data.dao.BaseDao;
import org.abondar.experimental.wsboard.base.data.dao.ContributorDao;
import org.abondar.experimental.wsboard.base.data.dao.ProjectDao;
import org.abondar.experimental.wsboard.base.data.dao.SprintDao;
import org.abondar.experimental.wsboard.base.data.dao.TaskDao;
import org.abondar.experimental.wsboard.base.data.dao.UserDao;
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
        return new UserDao(mapper(sqlSessionFactory), contributorDao(sqlSessionFactory));
    }

    @Bean(name = "projectDao")
    public BaseDao projectDao(SqlSessionFactory sqlSessionFactory) {
        return new ProjectDao(mapper(sqlSessionFactory));
    }

    @Bean(name="contributorDao")
    public BaseDao contributorDao(SqlSessionFactory sqlSessionFactory) {
        return new ContributorDao(mapper(sqlSessionFactory));
    }


    @Bean(name="taskDao")
    public BaseDao taskDao(SqlSessionFactory sqlSessionFactory) {
        return new TaskDao(mapper(sqlSessionFactory));
    }

    @Bean(name = "sprintDao")
    public BaseDao sprintDao(SqlSessionFactory sqlSessionFactory) {
        return new SprintDao(mapper(sqlSessionFactory));
    }

}
