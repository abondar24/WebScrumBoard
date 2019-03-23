module org.abondar.experimental.wsboard.base {

    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires org.abondar.experimental.wsboard.dataModel;
    requires org.mybatis;
    requires org.mybatis.spring;
    requires spring.context;
    requires spring.beans;
    requires java.xml.bind;
    requires slf4j.api;

    opens org.abondar.experimental.wsboard.base;
    opens org.abondar.experimental.wsboard.base.data;
    opens org.abondar.experimental.wsboard.base.data.dao;
    opens org.abondar.experimental.wsboard.base.password;
    opens org.abondar.experimental.wsboard.base.config;


}