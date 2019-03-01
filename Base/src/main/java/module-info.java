module org.abondar.experimental.wsboard.Base {

   requires spring.boot;
   requires spring.boot.autoconfigure;

   opens org.abondar.experimental.wsboard.base.dao;
}