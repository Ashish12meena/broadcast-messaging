package com.aigreentick.services.common.exception;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.aigreentick.services.auth.exception.EmailAlreadyExistsException;
import com.aigreentick.services.auth.exception.MobileNumberAlreadyExistsException;
import com.aigreentick.services.common.dto.ErrorResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        private ErrorResponse buildError(String status, String message, String path, HttpStatus statusCode) {
                return new ErrorResponse(status, message, path, LocalDateTime.now(), null);
        }

        @ExceptionHandler(InvalidFormatException.class)
        public ResponseEntity<ErrorResponse> handleInvalidEnumException(
                        InvalidFormatException ex,
                        HttpServletRequest request) {

                Map<String, String> errors = new HashMap<>();

                // Try to extract field name and target enum
                if (!ex.getPath().isEmpty()) {
                        String field = ex.getPath().get(0).getFieldName();
                        String invalidValue = String.valueOf(ex.getValue());

                        Class<?> targetType = ex.getTargetType();

                        // Check if it's an enum
                        if (targetType.isEnum()) {
                                String allowed = Arrays.stream(targetType.getEnumConstants())
                                                .map(Object::toString)
                                                .collect(Collectors.joining(", "));
                                errors.put(field, "Invalid value '" + invalidValue + "'. Allowed values: " + allowed);
                        } else {
                                errors.put(field, "Invalid value: " + invalidValue);
                        }
                }

                ErrorResponse errorResponse = new ErrorResponse(
                                "error",
                                "Validation failed",
                                request.getRequestURI(),
                                LocalDateTime.now(),
                                errors);

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationExceptions(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors().forEach(error -> {
                        errors.put(error.getField(), error.getDefaultMessage());
                });

                ErrorResponse errorResponse = new ErrorResponse(
                                "error",
                                "Validation failed",
                                request.getRequestURI(),
                                LocalDateTime.now(),
                                errors);

                log.warn("Validation failed at {}: {}", request.getRequestURI(), errors);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(UsernameNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUsernameNotFound(UsernameNotFoundException ex,
                        HttpServletRequest request) {
                log.warn("Username not found: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                buildError("error", ex.getMessage(), request.getRequestURI(), HttpStatus.NOT_FOUND));
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex,
                        HttpServletRequest request) {
                log.warn("Bad credentials: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                                buildError("error", "Invalid username or password", request.getRequestURI(),
                                                HttpStatus.UNAUTHORIZED));
        }

        @ExceptionHandler(AccountStatusException.class)
        public ResponseEntity<ErrorResponse> handleAccountStatus(AccountStatusException ex,
                        HttpServletRequest request) {
                log.warn("Account issue: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                                buildError("error", "Account is locked or disabled", request.getRequestURI(),
                                                HttpStatus.FORBIDDEN));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
                log.warn("Access denied: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                                buildError("error", "You are not authorized to access this resource",
                                                request.getRequestURI(),
                                                HttpStatus.FORBIDDEN));
        }

        @ExceptionHandler(SignatureException.class)
        public ResponseEntity<ErrorResponse> handleJwtSignature(SignatureException ex, HttpServletRequest request) {
                log.warn("Invalid JWT signature: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                                buildError("error", "Invalid JWT signature", request.getRequestURI(),
                                                HttpStatus.FORBIDDEN));
        }

        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException ex,
                        HttpServletRequest request) {
                log.warn("Upload size exceeded: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                                buildError("error", "File size exceeds the maximum limit!", request.getRequestURI(),
                                                HttpStatus.BAD_REQUEST));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
                log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                                buildError("error", "Internal server error", request.getRequestURI(),
                                                HttpStatus.INTERNAL_SERVER_ERROR));
        }

        @ExceptionHandler(MobileNumberAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleMobileExists(MobileNumberAlreadyExistsException ex,
                        HttpServletRequest request) {
                log.warn("Mobile number conflict: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                                buildError("error", ex.getMessage(), request.getRequestURI(), HttpStatus.CONFLICT));
        }

        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex,
                        HttpServletRequest request) {
                log.warn("Email conflict: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                                buildError("error", ex.getMessage(), request.getRequestURI(), HttpStatus.CONFLICT));
        }

        @ExceptionHandler(ResourceAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(ResourceAlreadyExistsException ex,
                        HttpServletRequest request) {
                log.warn("Resource conflict: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                                buildError("error", ex.getMessage(), request.getRequestURI(), HttpStatus.CONFLICT));
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
                        HttpServletRequest request) {
                log.warn("Resource not found: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                buildError("error", ex.getMessage(), request.getRequestURI(), HttpStatus.NOT_FOUND));
        }

}