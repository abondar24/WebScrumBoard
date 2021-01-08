package org.abondar.experimental.wsboard.server.config;


import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.abondar.experimental.wsboard.server.dao.BaseDao;
import org.abondar.experimental.wsboard.server.dao.ContributorDao;
import org.abondar.experimental.wsboard.server.dao.ProjectDao;
import org.abondar.experimental.wsboard.server.dao.SecurityCodeDao;
import org.abondar.experimental.wsboard.server.dao.SprintDao;
import org.abondar.experimental.wsboard.server.dao.TaskDao;
import org.abondar.experimental.wsboard.server.dao.UserDao;
import org.abondar.experimental.wsboard.server.mapper.DataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.jta.JtaTransactionManager;
/**
 * Data access objects configuration class
 *
 * @author a.bondar
 */
@Configuration
@Import({MapperConfig.class})
public class DaoConfig {

    @Autowired
    DataMapper dataMapper;

    @Bean
    @Qualifier("transactionManager")
    public JtaTransactionManager txManager() {
        var txManager = new UserTransactionManager();
        var transaction = new UserTransactionImp();
        return new JtaTransactionManager(transaction,txManager);
    }



    @Bean(name = "userDao")
    public UserDao userDao() throws Exception{
        return new UserDao(dataMapper,txManager());
    }

    @Bean(name = "projectDao")
    public ProjectDao projectDao() throws Exception{
        return new ProjectDao(dataMapper,txManager());
    }

    @Bean(name="contributorDao")
    public BaseDao contributorDao() throws Exception{
        return new ContributorDao(dataMapper);
    }

    @Bean(name="taskDao")
    public BaseDao taskDao() throws Exception{
        return new TaskDao(dataMapper);
    }

    @Bean(name = "sprintDao")
    public BaseDao sprintDao() throws Exception {
        return new SprintDao(dataMapper);
    }

    @Bean(name = "codeDao")
    public BaseDao codeDao() throws Exception{
        return new SecurityCodeDao(dataMapper);
    }


}
