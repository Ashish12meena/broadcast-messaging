package com.aigreentick.services.template.dto.buildTemplate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateCreateRequestDto {
    @NotNull(message = "wabaId cannot be null")
    private String wabaId;
    private String accessToken;
    private String apiVersion;
    private BaseTemplateRequestDto template;
}
