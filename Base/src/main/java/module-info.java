module org.abondar.experimental.wsboard.Base {

   requires spring.boot;
   requires spring.boot.autoconfigure;
   requires org.abondar.experimental.wsboard.DataModel;
   requires org.mybatis;
   requires org.mybatis.spring;

   opens org.abondar.experimental.wsboard.base;

}