package com.aigreentick.services.auth.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aigreentick.services.common.dto.ErrorResponse;
import com.aigreentick.services.common.exception.RoleAlreadyAssignedException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class AuthGlobaExceptionHandler {

    private ErrorResponse buildError(String status, String message, String path, HttpStatus statusCode) {
        return new ErrorResponse(status, message, path, LocalDateTime.now(), null);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex,
            HttpServletRequest request) {
        log.warn("User not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildError("error", ex.getMessage(), request.getRequestURI(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFound(RoleNotFoundException ex,
            HttpServletRequest request) {
        log.warn("Role not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildError("error", ex.getMessage(), request.getRequestURI(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(RoleAlreadyAssignedException.class)
    public ResponseEntity<ErrorResponse> handleRoleAlreadyAssigned(RoleAlreadyAssignedException ex,
            HttpServletRequest request) {
        log.warn("Role already assigned: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                buildError("error", ex.getMessage(), request.getRequestURI(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(PermissionAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePermissionAlreadyExists(PermissionAlreadyExistsException ex,
            HttpServletRequest request) {
        log.warn("Permission already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                buildError("error", ex.getMessage(), request.getRequestURI(), HttpStatus.BAD_REQUEST));
    }

}
