package com.aigreentick.services.messaging.exception;

public class UnauthorizedTemplateAccessException extends RuntimeException {
    public UnauthorizedTemplateAccessException(String message) {
        super(message);
    }
}