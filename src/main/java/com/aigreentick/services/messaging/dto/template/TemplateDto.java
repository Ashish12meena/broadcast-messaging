package com.aigreentick.services.messaging.dto.template;

import com.aigreentick.services.messaging.model.template.Template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDto {
    private Long id;
    private String name;
    private String category;
    private String language;
    private String status;

    public TemplateDto(Template template) {
        this.id = template.getId();
        this.name = template.getName();
        this.category = template.getCategory();
        this.language = template.getLanguage();
        this.status = template.getStatus().name();
    }
}
