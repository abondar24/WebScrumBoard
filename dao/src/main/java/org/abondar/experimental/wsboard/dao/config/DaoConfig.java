package org.abondar.experimental.wsboard.dao.config;

import org.abondar.experimental.wsboard.dao.BaseDao;
import org.abondar.experimental.wsboard.dao.ContributorDao;
import org.abondar.experimental.wsboard.dao.ProjectDao;
import org.abondar.experimental.wsboard.dao.SprintDao;
import org.abondar.experimental.wsboard.dao.TaskDao;
import org.abondar.experimental.wsboard.dao.UserDao;
import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoConfig {
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
