package org.abondar.experimental.wsboard.base;


import org.abondar.experimental.wsboard.base.config.BaseConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"org.abondar.experimental.wsboard.base.data"})
@ImportAutoConfiguration({BaseConfig.class})
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }


}
