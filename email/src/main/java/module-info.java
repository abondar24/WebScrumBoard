module org.abondar.experimental.wsboard.email {

    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;
    requires camel.core;
    requires camel.mail;

    exports org.abondar.experimental.wsboard.email.route;
}
