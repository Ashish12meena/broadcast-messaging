package com.aigreentick.services.template.dto.buildTemplate;

import java.util.List;

import com.aigreentick.services.template.dto.TemplateComponentRequestDto;

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
