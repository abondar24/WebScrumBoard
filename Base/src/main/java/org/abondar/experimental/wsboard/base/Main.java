package org.abondar.experimental.wsboard.base;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"org.abondar.experimental.wsboard.dao"})
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }
}
