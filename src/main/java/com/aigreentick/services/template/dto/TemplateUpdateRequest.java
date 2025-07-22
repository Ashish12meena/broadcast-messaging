package com.aigreentick.services.template.dto;

import java.util.List;
import java.util.Map;

import com.aigreentick.services.template.model.TemplateComponent;

import lombok.Data;

@Data
public class TemplateUpdateRequest {
    private String name;
    private String language;
    private String category;
    private String previousCategory;
    private String whatsappId;
    private String payload;
    private List<TemplateComponent> components;
    private Map<String, Object> response;
}
