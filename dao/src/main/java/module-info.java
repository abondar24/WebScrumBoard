module org.abondar.experimental.wsboard.dao {
    requires org.abondar.experimental.wsboard.dataModel;
    requires org.mybatis;
    requires org.mybatis.spring;
    requires spring.context;
    requires spring.beans;
    requires slf4j.api;
    requires java.xml.bind;


    exports org.abondar.experimental.wsboard.dao.config;
    exports org.abondar.experimental.wsboard.dao.data;
    exports org.abondar.experimental.wsboard.dao.password;
    exports org.abondar.experimental.wsboard.dao.password.exception;
    exports org.abondar.experimental.wsboard.dao;
}
