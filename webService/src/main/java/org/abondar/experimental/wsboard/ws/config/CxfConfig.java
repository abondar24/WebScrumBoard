package org.abondar.experimental.wsboard.ws.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.ws.impl.AuthServiceImpl;
import org.abondar.experimental.wsboard.ws.impl.ContributorServiceImpl;
import org.abondar.experimental.wsboard.ws.impl.ProjectServiceImpl;
import org.abondar.experimental.wsboard.ws.impl.SprintServiceImpl;
import org.abondar.experimental.wsboard.ws.impl.TaskServiceImpl;
import org.abondar.experimental.wsboard.ws.impl.UserServiceImpl;
import org.abondar.experimental.wsboard.ws.security.TokenExpiredMapper;
import org.abondar.experimental.wsboard.ws.security.TokenRenewalFilter;
import org.abondar.experimental.wsboard.ws.service.AuthService;
import org.abondar.experimental.wsboard.ws.service.RestService;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.interceptor.security.SecureAnnotationsInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm;
import org.apache.cxf.rs.security.jose.jws.HmacJwsSignatureVerifier;
import org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rest server configuration class
 *
 * @author a.bondar
 */
@Configuration
@Component
public class CxfConfig implements WebMvcConfigurer {


    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public PreferencesPlaceholderConfigurer configurer() {
        return new PreferencesPlaceholderConfigurer();
    }

    @Bean
    public RestService userService(AuthService authService) {
        return new UserServiceImpl(authService);
    }

    @Bean
    public RestService projectService() {
        return new ProjectServiceImpl();
    }

    @Bean
    RestService contributorService() {
        return new ContributorServiceImpl();
    }

    @Bean
    public RestService taskService() {
        return new TaskServiceImpl();
    }

    @Bean
    public RestService sprintService() {
        return new SprintServiceImpl();
    }

    @Bean
    public AuthService authService() {
        return new AuthServiceImpl();
    }


    @Bean
    public JAXRSServerFactoryBean jaxRsServer(JacksonJsonProvider jsonProvider, AuthService authService) {

        var factory = new JAXRSServerFactoryBean();
        factory.setBus(springBus());
        factory.setServiceBeanObjects(userService(authService), projectService(),
                contributorService(), taskService(), sprintService());

        factory.setProviders(List.of(jsonProvider, authenticationFilter(authService)));
        factory.setInInterceptors(List.of(new LoggingInInterceptor(), secureAnnotationsInterceptor()));
        factory.setOutInterceptors(List.of(new LoggingOutInterceptor()));


        factory.setFeatures(List.of(createSwaggerFeature()));
        Map<Object, Object> extMappings = new HashMap<>();
        extMappings.put("json", "application/json");
        factory.setExtensionMappings(extMappings);
        Map<Object, Object> langMappings = new HashMap<>();
        langMappings.put("en", "en-gb");
        factory.setLanguageMappings(langMappings);
        factory.setAddress("/wsboard");

        return factory;
    }


    @Bean
    public JacksonJsonProvider jsonProvider() {
        var provider = new JacksonJsonProvider();
        provider.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        provider.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
        return provider;
    }


    @Bean
    public Swagger2Feature createSwaggerFeature() {
        var swagger2Feature = new Swagger2Feature();
        swagger2Feature.setPrettyPrint(true);
        swagger2Feature.setHost("localhost:8024");
        swagger2Feature.setBasePath("/cxf/wsboard/api");
        swagger2Feature.setSupportSwaggerUi(true);
        swagger2Feature.setRunAsFilter(true);

        swagger2Feature.setTitle("Web Scrum Board");
        swagger2Feature.setDescription("Web Scrum Board REST API");
        swagger2Feature.setContact("Alex Bondar(abondar1992@gmail.com)");
        swagger2Feature.setLicense("MIT");


        return swagger2Feature;
    }


    @Bean
    public TokenRenewalFilter authenticationFilter(AuthService authService) {
        var filter = new TokenRenewalFilter();
        filter.setAuthService(authService);
        filter.setJweRequired(true);
        filter.setJwsVerifier(signatureVerifier());

        return filter;
    }

    @Bean
    public HmacJwsSignatureVerifier signatureVerifier() {
        return new HmacJwsSignatureVerifier("Ym9yc2NodA", SignatureAlgorithm.HS256);
    }


    @Bean
    public TokenExpiredMapper exceptionMapper() {
        return new TokenExpiredMapper();
    }


    @Bean
    public SecureAnnotationsInterceptor secureAnnotationsInterceptor() {
        return new SecureAnnotationsInterceptor();
    }


}
