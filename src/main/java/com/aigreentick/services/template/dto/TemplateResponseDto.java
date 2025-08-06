package com.aigreentick.services.template.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateResponseDto {
    private String id;
    private String name;
    private String status;
    private String category;
    private String language;
     private String metaTemplateId;
}