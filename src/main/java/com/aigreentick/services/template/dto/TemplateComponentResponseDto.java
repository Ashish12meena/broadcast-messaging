package com.aigreentick.services.template.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateComponentResponseDto {
    private Long id;
    private Long templateId;
    private String type;
    private String format;
    private String text;
    private String imageUrl;
    private List<TemplateComponentButtonResponseDto> buttons;
}