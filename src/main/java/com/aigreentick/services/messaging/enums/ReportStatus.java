package com.aigreentick.services.messaging.enums;

public enum ReportStatus {
    PENDING,        
    SUCCESS,
    FAILED,         // Failed to send
    BLOCKED,        // Blocked by WhatsApp or recipient
    INVALID_NUMBER, // Invalid phone number
    DELETED         // Soft-deleted (if needed in filters)
}
