package com.aigreentick.services.common.enums;

public enum MessageStatusEnum {
    PENDING,      // Not yet sent to WhatsApp
    SENT,         // Accepted by WhatsApp API
    DELIVERED,    // Delivered to recipient
    READ,         
    FAILED        
}
