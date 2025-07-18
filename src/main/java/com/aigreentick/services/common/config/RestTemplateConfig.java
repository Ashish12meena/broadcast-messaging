package com.aigreentick.services.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean(name = "metaRestTemplate")
    public RestTemplate metaRestTemplate(
            @Value("${timeout.meta.connect}") int connectTimeout,
            @Value("${timeout.meta.read}") int readTimeout) {

        return createRestTemplate(connectTimeout, readTimeout);
    }

    @Bean(name = "analyticsRestTemplate")
    public RestTemplate analyticsRestTemplate(
            @Value("${timeout.analytics.connect}") int connectTimeout,
            @Value("${timeout.analytics.read}") int readTimeout) {

        return createRestTemplate(connectTimeout, readTimeout);
    }

    private RestTemplate createRestTemplate(int connectTimeout, int readTimeout) {
        return new RestTemplate();
    }
}
