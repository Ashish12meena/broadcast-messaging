package com.aigreentick.services.messaging.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateComponentDto {
    private Long id;
    private Long templateId;
    private String type;
    private String format;
    private String text;
    private String imageUrl;
    private List<TemplateComponentButtonDto> buttons;
}