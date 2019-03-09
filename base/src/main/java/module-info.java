module org.abondar.experimental.wsboard.base {

   requires spring.boot;
   requires spring.boot.autoconfigure;
   requires org.abondar.experimental.wsboard.dataModel;
   requires org.mybatis;
   requires org.mybatis.spring;
   requires spring.context;
   requires spring.beans;
   requires java.xml.bind;

   opens org.abondar.experimental.wsboard.base;
   opens org.abondar.experimental.wsboard.base.dao;
   opens org.abondar.experimental.wsboard.base.password;



}