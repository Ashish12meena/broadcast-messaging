package com.aigreentick.services.whatsapp.dto;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsappTemplateComponentRequestDto {
    private String type;
    private String format;
    private String text;
    private String imageUrl;
    private List<WhatsappTemplateComponentButtonRequestDto> buttons;
}
