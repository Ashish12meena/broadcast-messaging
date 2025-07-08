package com.aigreentick.services.whatsapp.config;

import java.util.concurrent.Semaphore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aigreentick.services.messaging.constants.MessagingConstants;

@Configuration
public class WhatsappConcurrencyConfig {
    @Bean
    public Semaphore whatsappConcurrencySemaphore() {
        return new Semaphore(MessagingConstants.MAX_CONCURRENT_WHATSAPP_REQUESTS);  
    }
}

