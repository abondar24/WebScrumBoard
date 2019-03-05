module org.abondar.experimental.wsboard.base {

   requires spring.boot;
   requires spring.boot.autoconfigure;
   requires org.abondar.experimental.wsboard.dataModel;
   requires org.mybatis;
   requires org.mybatis.spring;

   opens org.abondar.experimental.wsboard.base.dao;

}