package com.aigreentick.services.template.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTemplateResponseDto {
    private String id;
    private String name;
    private String status;
    private String category;
    private String language;
     private String metaTemplateId;
}
