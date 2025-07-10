package com.aigreentick.services.messaging.dto.template;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateComponentButtonDto {
     private Long id;
    private Long templateId;
    private Long componentId;
    private String type;
    private String number;
    private String text;
    private String url;
}
