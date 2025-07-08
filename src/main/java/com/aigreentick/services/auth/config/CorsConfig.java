package com.aigreentick.services.auth.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
    @Value("${cors.allowed.origins}")
    private String origins;

    @Value("${cors.allowed.methods}")
    private String allowedMethods;

    @Value("${cors.allowed.headers}")
    private String allowedHeaders;

    @Value("${cors.allow.credentials}")
    private boolean allowCredentials;
        // CORS Configuration Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        List<String> originList = Arrays.asList(origins.split(","));
        List<String> methodList = Arrays.asList(allowedMethods.split(","));
        List<String> headerList = Arrays.asList(allowedHeaders.split(","));

        corsConfig.setAllowedOrigins(originList);
        corsConfig.setAllowedMethods(methodList);
        corsConfig.setAllowedHeaders(headerList);
        corsConfig.setAllowCredentials(allowCredentials);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}
