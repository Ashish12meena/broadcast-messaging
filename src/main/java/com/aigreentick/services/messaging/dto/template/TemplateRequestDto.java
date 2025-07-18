package com.aigreentick.services.messaging.dto.template;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequestDto {
    private String name;
    private String language;
    private String category;
    private List<TemplateComponentRequestDto> components;
    private String wabaId;
    private String accessToken;
    private String apiVersion; 
}
