module org.abondar.experimental.wsboard.base {

    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;

    requires org.abondar.experimental.wsboard.webService;
    requires transitive org.abondar.experimental.wsboard.dao;
    requires transitive org.abondar.experimental.wsboard.email;

    requires org.mybatis;
    requires org.mybatis.spring;
    requires slf4j.api;


}
