module org.abondar.experimental.wsboard.webService {

    requires transitive org.abondar.experimental.wsboard.dataModel;
    requires org.abondar.experimental.wsboard.dao;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;
    requires spring.webmvc;
    requires slf4j.api;
    requires org.apache.cxf.rs.swagger;
    requires org.apache.cxf.spring.boot.autoconfigure;
    requires org.apache.cxf.core;
    requires org.apache.cxf.frontend.jaxrs;
    requires org.apache.cxf.transport.http;
    requires org.apache.cxf.transport.local;
    requires org.apache.cxf.rs.security.jose;
    requires org.apache.cxf.rs.security.jose.jaxrs;
    requires org.apache.cxf.logging;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.jaxrs.json;
    requires java.ws.rs;
    requires javax.servlet.api;
    requires java.annotation;
    requires io.swagger.v3.core;
    requires io.swagger.v3.jaxrs2;
    requires io.swagger.v3.oas.annotations;


    exports org.abondar.experimental.wsboard.ws.config;
    exports org.abondar.experimental.wsboard.ws.service;
    exports org.abondar.experimental.wsboard.ws.security;
}
