package com.aigreentick.services.template.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateComponentButtonResponseDto {
     private Long id;
    private Long templateId;
    private Long componentId;
    private String type;
    private String number;
    private String text;
    private String url;
}
