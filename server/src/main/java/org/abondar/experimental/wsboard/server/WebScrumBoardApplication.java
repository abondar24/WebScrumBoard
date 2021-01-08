package org.abondar.experimental.wsboard.server;



import org.abondar.experimental.wsboard.server.config.CxfConfig;
import org.abondar.experimental.wsboard.server.config.TransactionConfig;
import org.abondar.experimental.wsboard.server.config.RouteConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
//@MapperScan({"org.abondar.experimental.wsboard.server.mapper"})
@ImportAutoConfiguration({TransactionConfig.class, CxfConfig.class, RouteConfig.class})
@EnableConfigurationProperties(DataSourceProperties.class)
public class WebScrumBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebScrumBoardApplication.class);
    }


}
