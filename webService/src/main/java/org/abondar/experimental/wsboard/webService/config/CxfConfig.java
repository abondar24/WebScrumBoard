package org.abondar.experimental.wsboard.webService.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.abondar.experimental.wsboard.webService.impl.AuthServiceImpl;
import org.abondar.experimental.wsboard.webService.impl.ProjectServiceImpl;
import org.abondar.experimental.wsboard.webService.impl.UserServiceImpl;
import org.abondar.experimental.wsboard.webService.security.TokenExpiredMapper;
import org.abondar.experimental.wsboard.webService.security.TokenRenewalFilter;
import org.abondar.experimental.wsboard.webService.service.AuthService;
import org.abondar.experimental.wsboard.webService.service.RestService;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.interceptor.security.SecureAnnotationsInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm;
import org.apache.cxf.rs.security.jose.jws.HmacJwsSignatureVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Component
public class CxfConfig implements WebMvcConfigurer {

    @Value("${cxf.jwt.signature}")
    private String sig = "Ym9yc2NodA";

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    PreferencesPlaceholderConfigurer configurer() {
        return new PreferencesPlaceholderConfigurer();
    }

    @Bean
    RestService userService(AuthService authService) {
        return new UserServiceImpl(authService);
    }

    @Bean
    RestService projectService() {
        return new ProjectServiceImpl();
    }

    @Bean
    AuthService authService() {
        return new AuthServiceImpl();
    }


    @Bean
    public Server jaxRsServer(JacksonJsonProvider jsonProvider, AuthService authService) {


        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBus(springBus());
        factory.setServiceBeanObjects(userService(authService), projectService());

        factory.setProvider(jsonProvider);
        factory.setProvider(authenticationFilter(authService));
        factory.setInInterceptors(Arrays.asList(new LoggingOutInterceptor(), secureAnnotationsInterceptor()));
        factory.setOutInterceptors(Collections.singletonList(new LoggingOutInterceptor()));

        factory.setFeatures(Collections.singletonList(createSwaggerFeature()));
        Map<Object, Object> extMappings = new HashMap<>();
        extMappings.put("json", "application/json");
        factory.setExtensionMappings(extMappings);
        Map<Object, Object> langMappings = new HashMap<>();
        langMappings.put("en", "en-gb");
        factory.setLanguageMappings(langMappings);
        factory.setAddress("/wsboard");

        return factory.create();
    }


    @Bean
    public JacksonJsonProvider jsonProvider() {
        JacksonJsonProvider provider = new JacksonJsonProvider();
        provider.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        provider.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
        return provider;
    }


    @Bean
    public Swagger2Feature createSwaggerFeature() {
        Swagger2Feature swagger2Feature = new Swagger2Feature();
        swagger2Feature.setPrettyPrint(true);
        swagger2Feature.setHost("localhost:8024");
        swagger2Feature.setBasePath("/cxf/wsboard");
        return swagger2Feature;
    }


    @Bean
    public TokenRenewalFilter authenticationFilter(AuthService authService) {
        TokenRenewalFilter filter = new TokenRenewalFilter();
        filter.setAuthService(authService);
        filter.setJweRequired(true);
        filter.setJwsVerifier(signatureVerifier());

        return filter;
    }

    @Bean
    public HmacJwsSignatureVerifier signatureVerifier() {
        return new HmacJwsSignatureVerifier(sig, SignatureAlgorithm.HS256);
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
