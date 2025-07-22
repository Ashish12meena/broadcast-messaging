package com.aigreentick.services.template.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateResponseDto {
    private Long id;
    private String name;
    private String status;
    private String category;
    private String email;
    private String language;
    private String payload;
     private String WhatsappId;
     private String metaTemplateId;
     private List<TemplateComponentResponseDto> components;
}