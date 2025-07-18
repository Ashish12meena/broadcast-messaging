package com.aigreentick.services.whatsapp.dto;

import java.util.List;
import lombok.Data;

@Data
public class WhatsappTemplateRequestDto {
     private String name;
    private String language;
    private String category;
    private List<WhatsappTemplateComponentRequestDto> components;
    private String wabaId;
    private String accessToken;
    private String apiVersion; 
}
