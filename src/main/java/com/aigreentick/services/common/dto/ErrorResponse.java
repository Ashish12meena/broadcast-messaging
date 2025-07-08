package com.aigreentick.services.common.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String status;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, String> errors;
}
