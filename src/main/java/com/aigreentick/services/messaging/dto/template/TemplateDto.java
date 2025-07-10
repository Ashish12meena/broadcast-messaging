package com.aigreentick.services.messaging.dto.template;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateDto {
    private Long id;
    private Long userId;
    private String name;
    private String status;
    private String category;
    private String email;
    private String language;
    private String payload;
     private String waId;
     private List<TemplateComponentDto> components;
}