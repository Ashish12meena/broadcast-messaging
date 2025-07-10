package com.aigreentick.services.messaging.dto.template;

import java.time.LocalDateTime;

import com.aigreentick.services.messaging.enums.TemplateStatus;

import lombok.Data;

@Data
public class TemplateFilterRequestDto {
    private Long userId;
    private String name;
    private String language;
    private String category;
    private TemplateStatus status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}