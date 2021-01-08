package org.abondar.experimental.wsboard.base;


import org.abondar.experimental.wsboard.base.config.DaoConfig;
import org.abondar.experimental.wsboard.ws.config.CxfConfig;
import org.abondar.experimental.wsboard.ws.config.RouteConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@MapperScan({"org.abondar.experimental.wsboard.dao.data"})
@ImportAutoConfiguration({DaoConfig.class, CxfConfig.class, RouteConfig.class})
@EnableConfigurationProperties(DataSourceProperties.class)
public class WebScrumBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebScrumBoardApplication.class);
    }


}
