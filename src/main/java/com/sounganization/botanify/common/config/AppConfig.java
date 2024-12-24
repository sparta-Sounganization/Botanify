package com.sounganization.botanify.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class AppConfig {

    @Bean(name = "weatherRestTemplate")
    public RestTemplate weatherRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY); // 쿼리 파라미터만 인코딩
        restTemplate.setUriTemplateHandler(factory);
        return restTemplate;
    }

    @Bean(name = "locationRestTemplate")
    public RestTemplate locationRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);
        restTemplate.setUriTemplateHandler(factory);
        return restTemplate;
    }
}
