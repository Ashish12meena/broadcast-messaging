package com.aigreentick.services.whatsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsappTemplateComponentButtonRequestDto {
    private String type;
    private String number;
    private String text;
    private String url;
}