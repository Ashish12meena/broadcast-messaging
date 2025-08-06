package com.aigreentick.services.template.dto;



import lombok.Data;

@Data
public class TemplateUpdateRequest {
    private String name;
    private String language;
    private String category;
    private String previousCategory;
    // private List<TemplateComponent> components;
}
