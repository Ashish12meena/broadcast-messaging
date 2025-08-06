package com.aigreentick.services.template.dto;

import com.aigreentick.services.template.dto.buildTemplate.BaseTemplateRequestDto;
import com.aigreentick.services.template.model.Template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDto {
    private String id;
    private String name;
    private String category;
    private String language;
    private String status;
    private BaseTemplateRequestDto template;

    public TemplateDto(Template template) {
        this.id = template.getId();
        this.name = template.getName();
        this.category = template.getCategory();
        this.language = template.getLanguage();
        this.status = template.getStatus().name();
    }
}
