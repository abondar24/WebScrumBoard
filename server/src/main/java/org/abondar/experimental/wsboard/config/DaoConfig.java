package org.abondar.experimental.wsboard.base.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.abondar.experimental.wsboard.base.dao.BaseDao;
import org.abondar.experimental.wsboard.base.dao.ContributorDao;
import org.abondar.experimental.wsboard.base.dao.ProjectDao;
import org.abondar.experimental.wsboard.base.dao.SecurityCodeDao;
import org.abondar.experimental.wsboard.base.dao.SprintDao;
import org.abondar.experimental.wsboard.base.dao.TaskDao;
import org.abondar.experimental.wsboard.base.dao.UserDao;
import org.abondar.experimental.wsboard.base.mapper.DataMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
/**
 * Data access objects configuration class
 *
 * @author a.bondar
 */
@Configuration
public class DaoConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String driverClass;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;



    @Bean
    DataSource dataSource(){
        var dsBuilder = DataSourceBuilder.create();
        dsBuilder.driverClassName(driverClass);
        dsBuilder.url(url);
        dsBuilder.username(username);
        dsBuilder.password(password);

        return  dsBuilder.build();
    }


    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception{
        SqlSessionFactoryBean sf = new SqlSessionFactoryBean();
        sf.setDataSource(dataSource());
        return sf.getObject();
    }

    @Bean
    @Qualifier("transactionManager")
    public JtaTransactionManager txManager() {
        var txManager = new UserTransactionManager();
        var transaction = new UserTransactionImp();
        return new JtaTransactionManager(transaction,txManager);
    }

    @Bean
    public DataMapper dataMapper() throws Exception {
       var sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory());
        return sqlSessionTemplate.getMapper(DataMapper.class);
    }


//    @Bean
//    public DataMapper dataMapper() throws Exception {
//        MapperFactoryBean<DataMapper> factoryBean = new MapperFactoryBean<>(DataMapper.class);
//        factoryBean.setSqlSessionFactory(sqlSessionFactory());
//        return factoryBean.getObject();
//    }

    @Bean(name = "userDao")
    public UserDao userDao() throws Exception{
        return new UserDao(dataMapper(),txManager());
    }

    @Bean(name = "projectDao")
    public ProjectDao projectDao() throws Exception{
        return new ProjectDao(dataMapper(),txManager());
    }

    @Bean(name="contributorDao")
    public BaseDao contributorDao() throws Exception{
        return new ContributorDao(dataMapper());
    }

    @Bean(name="taskDao")
    public BaseDao taskDao() throws Exception{
        return new TaskDao(dataMapper());
    }

    @Bean(name = "sprintDao")
    public BaseDao sprintDao() throws Exception {
        return new SprintDao(dataMapper());
    }

    @Bean(name = "codeDao")
    public BaseDao codeDao() throws Exception{
        return new SecurityCodeDao(dataMapper());
    }


}
