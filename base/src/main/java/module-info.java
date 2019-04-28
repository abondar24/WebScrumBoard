module org.abondar.experimental.wsboard.base {

    requires spring.boot;
    requires spring.boot.autoconfigure;
    //requires org.abondar.experimental.wsboard.webService;
    requires org.abondar.experimental.wsboard.dao;
    requires org.mybatis;
    requires org.mybatis.spring;
    requires spring.context;
    requires spring.beans;
    requires slf4j.api;


}
