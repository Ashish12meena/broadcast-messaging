package com.aigreentick.services.whatsapp.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.aigreentick.services.common.enums.MessageStatusEnum;

@Service
public class WhatsappServiceImpl {
     public Map<String, Object> sendMessage(String mobile, String message) {
        try {
            Thread.sleep(500 + new Random().nextInt(300)); // Simulate 200ms–500ms delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("status", MessageStatusEnum.DELIVERED);
        response.put("messageId", UUID.randomUUID().toString());
        response.put("waId", UUID.randomUUID().toString());
        return response;
    }

}

