package org.abondar.experimental.wsboard.dao.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.abondar.experimental.wsboard.dao.BaseDao;
import org.abondar.experimental.wsboard.dao.ContributorDao;
import org.abondar.experimental.wsboard.dao.ProjectDao;
import org.abondar.experimental.wsboard.dao.SecurityCodeDao;
import org.abondar.experimental.wsboard.dao.SprintDao;
import org.abondar.experimental.wsboard.dao.TaskDao;
import org.abondar.experimental.wsboard.dao.UserDao;
import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * Data access objects configuration class
 *
 * @author a.bondar
 */
@Configuration
public class DaoConfig {
    @Bean
    public DataMapper mapper(SqlSessionFactory sqlSessionFactory) {
        SqlSessionTemplate sessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        return sessionTemplate.getMapper(DataMapper.class);
    }

    @Bean
    @Qualifier("transactionManager")
    public JtaTransactionManager txManager() {
        var txManager = new UserTransactionManager();
        var transaction = new UserTransactionImp();
        return new JtaTransactionManager(transaction,txManager);
    }



    @Bean(name = "userDao")
    public BaseDao userDao(SqlSessionFactory sqlSessionFactory) {
        return new UserDao(mapper(sqlSessionFactory), txManager());
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

    @Bean(name = "codeDao")
    public BaseDao codeDao(SqlSessionFactory sqlSessionFactory) {
        return new SecurityCodeDao(mapper(sqlSessionFactory));
    }


}
