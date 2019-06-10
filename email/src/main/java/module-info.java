module org.abondar.experimental.wsboard.email {

    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;
    requires camel.core;

    exports org.abondar.experimental.wsboard.email.route;
}
