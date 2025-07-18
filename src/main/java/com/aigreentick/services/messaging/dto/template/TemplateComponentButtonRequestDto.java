package com.aigreentick.services.messaging.dto.template;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateComponentButtonRequestDto {
    private String type;
    private String number;
    private String text;
    private String url;
}
