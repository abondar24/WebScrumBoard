package org.abondar.experimental.wsboard.base;


import org.abondar.experimental.wsboard.dao.config.DaoConfig;
import org.abondar.experimental.wsboard.webService.config.CxfConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan({"org.abondar.experimental.wsboard.dao.data"})
@ImportAutoConfiguration({DaoConfig.class, CxfConfig.class})
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }


}
