package com.aigreentick.services.template.dto;

import java.time.LocalDateTime;

import com.aigreentick.services.template.enums.TemplateStatus;

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