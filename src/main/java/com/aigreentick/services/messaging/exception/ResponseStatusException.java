package com.aigreentick.services.messaging.exception;

import org.springframework.http.HttpStatus;

public class ResponseStatusException extends RuntimeException {
    private final HttpStatus statusCode;

    public ResponseStatusException(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
    
}
