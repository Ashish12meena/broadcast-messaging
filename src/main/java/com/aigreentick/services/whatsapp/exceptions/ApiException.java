package com.aigreentick.services.whatsapp.exceptions;

public class ApiException extends RuntimeException {
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
