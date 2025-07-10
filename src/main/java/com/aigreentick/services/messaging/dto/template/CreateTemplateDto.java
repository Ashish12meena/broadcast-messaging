package com.aigreentick.services.messaging.dto.template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTemplateDto {
    private String name;
    private String previousCategory;
    private String category;
    private String payload;
    private String language;
}
