package org.abondar.experimental.wsboard.server.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.server.mapper.TokenExpiredMapper;
import org.abondar.experimental.wsboard.server.mapper.UserMapper;
import org.abondar.experimental.wsboard.server.security.TokenRenewalFilter;
import org.abondar.experimental.wsboard.server.service.AuthService;
import org.abondar.experimental.wsboard.server.service.AuthServiceImpl;
import org.abondar.experimental.wsboard.server.service.ContributorService;
import org.abondar.experimental.wsboard.server.service.ProjectService;
import org.abondar.experimental.wsboard.server.service.SprintService;
import org.abondar.experimental.wsboard.server.service.TaskService;
import org.abondar.experimental.wsboard.server.service.UserService;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm;
import org.apache.cxf.rs.security.jose.jws.HmacJwsSignatureVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
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
public class  CxfConfig implements WebMvcConfigurer {

    @Autowired
    private UserMapper userMapper;


    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public PreferencesPlaceholderConfigurer configurer() {
        return new PreferencesPlaceholderConfigurer();
    }


    @Bean
    public AuthService authService() {
        return new AuthServiceImpl(userMapper);
    }


    @Bean
    public JAXRSServerFactoryBean jaxRsServer(JacksonJsonProvider jsonProvider) {

        var factory = new JAXRSServerFactoryBean();
        factory.setBus(springBus());
        factory.setResourceClasses(List.of(UserService.class, ProjectService.class, ContributorService.class,
                TaskService.class, SprintService.class));


        factory.setProviders(List.of(jsonProvider, authenticationFilter(), expiredMapper()));
        factory.setInInterceptors(List.of(new LoggingInInterceptor()));
        factory.setOutInterceptors(List.of(new LoggingOutInterceptor()));


        factory.setFeatures(List.of(createSwaggerFeature()));
        Map<Object, Object> extMappings = new HashMap<>();
        extMappings.put("json", "application/json");
        factory.setExtensionMappings(extMappings);
        Map<Object, Object> langMappings = new HashMap<>();
        langMappings.put("en", "en-us");
        langMappings.put("ru", "ru-ru");
        langMappings.put("de", "de-de");
        langMappings.put("fr", "fr-fr");
        langMappings.put("es", "es-es");


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
        swagger2Feature.setSupportSwaggerUi(true);
        swagger2Feature.setRunAsFilter(true);

        swagger2Feature.setTitle("Web Scrum Board");
        swagger2Feature.setDescription("Web Scrum Board REST API");
        swagger2Feature.setContact("Alex Bondar(abondar1992@gmail.com)");
        swagger2Feature.setLicense("MIT");


        return swagger2Feature;
    }


    @Bean
    public TokenRenewalFilter authenticationFilter() {
        var filter = new TokenRenewalFilter();
        filter.setJweRequired(false);
        filter.setJwsVerifier(signatureVerifier());

        return filter;
    }

    @Bean
    public HmacJwsSignatureVerifier signatureVerifier() {
        return new HmacJwsSignatureVerifier("Ym9yc2NodA", SignatureAlgorithm.HS256);
    }

    @Bean
    MessageSource messageSource() {

        var msgSrc = new ResourceBundleMessageSource();
        msgSrc.setBasenames("messages");
        return msgSrc;
    }

    @Bean
    public TokenExpiredMapper expiredMapper() {
        return new TokenExpiredMapper(messageSource());
    }


}
