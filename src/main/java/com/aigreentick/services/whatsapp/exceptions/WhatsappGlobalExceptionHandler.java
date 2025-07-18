package com.aigreentick.services.whatsapp.exceptions;

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
public class WhatsappGlobalExceptionHandler {
    private ErrorResponse buildError(String status, String message, String path, HttpStatus statusCode) {
        return new ErrorResponse(status, message, path, LocalDateTime.now(), null);
    }

    @ExceptionHandler(WhatsAppMessageException.class)
    public ResponseEntity<ErrorResponse> handleWhatsAppMessageException(WhatsAppMessageException ex,
            HttpServletRequest request) {
        log.warn("WhatsApp message send failed: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                buildError("error", ex.getMessage(), request.getRequestURI(), HttpStatus.BAD_GATEWAY));
    }
}
