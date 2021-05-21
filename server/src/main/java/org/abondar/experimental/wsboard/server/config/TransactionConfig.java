package org.abondar.experimental.wsboard.server.config;


import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
/**
 * Data access objects configuration class
 *
 * @author a.bondar
 */
@Configuration
public class TransactionConfig {

    @Bean
    @Qualifier("transactionManager")
    public PlatformTransactionManager txManager() {
        var txManager = new UserTransactionManager();
        var transaction = new UserTransactionImp();
        return new JtaTransactionManager(transaction,txManager);
    }

}
