package com.aigreentick.services.whatsapp.dto;

import java.util.List;

import com.aigreentick.services.template.dto.TemplateComponentRequestDto;

import lombok.Data;

@Data
public class FacebookTemplateRequest {
     private String name;
    private String language;
    private String category;
    private List<TemplateComponentRequestDto> components;
}
