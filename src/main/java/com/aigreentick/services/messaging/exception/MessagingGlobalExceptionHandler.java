package com.aigreentick.services.messaging.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aigreentick.services.common.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class MessagingGlobalExceptionHandler {
    private ErrorResponse buildError(String status, String message, String path, HttpStatus statusCode) {
        return new ErrorResponse(status, message, path, LocalDateTime.now(), null);
    }

    

   

   

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex,
            HttpServletRequest request) {
        log.warn("status response exception : {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(
                buildError("error", ex.getMessage(), request.getRequestURI(), ex.getStatusCode()));
    }

}
