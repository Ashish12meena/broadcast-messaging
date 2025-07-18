package com.aigreentick.services.messaging.dto.template;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateComponentRequestDto {
    private String type;
    private String format;
    private String text;
    private String imageUrl;
    private List<TemplateComponentButtonRequestDto> buttons;
}
