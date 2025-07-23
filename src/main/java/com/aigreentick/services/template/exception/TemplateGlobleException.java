package com.aigreentick.services.template.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aigreentick.services.common.dto.ErrorResponse;
import com.aigreentick.services.messaging.exception.UnauthorizedTemplateAccessException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class TemplateGlobleException {
    private ErrorResponse buildError(String status, String message, String path, HttpStatus statusCode) {
        return new ErrorResponse(status, message, path, LocalDateTime.now(), null);
    }

    @ExceptionHandler(TemplateAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleTemplateExists(TemplateAlreadyExistsException ex,
            HttpServletRequest request) {
        log.warn("template conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                buildError("error", ex.getMessage(), request.getRequestURI(), HttpStatus.CONFLICT));
    }

     @ExceptionHandler(UnauthorizedTemplateAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedTemplateAccessException(UnauthorizedTemplateAccessException ex,
            HttpServletRequest request) {
        log.warn("template forbidden: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                buildError("error", ex.getMessage(), request.getRequestURI(), HttpStatus.FORBIDDEN));
    }

     @ExceptionHandler(TemplateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTemplateNotExists(TemplateNotFoundException ex,
            HttpServletRequest request) {
        log.warn("template conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildError("error", ex.getMessage(), request.getRequestURI(), HttpStatus.NOT_FOUND));
    }
}
