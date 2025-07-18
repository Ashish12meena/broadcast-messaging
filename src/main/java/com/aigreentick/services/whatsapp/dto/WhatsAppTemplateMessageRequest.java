package com.aigreentick.services.whatsapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppTemplateMessageRequest {
    @NotBlank
    private String recipientNumber;

    @NotBlank
    private String templateName;

    @NotBlank
    private String languageCode;

    @NotBlank
    private String parametersJson; // Should be a valid JSON array string of parameters

    @NotBlank
    private String apiVersion;

    @NotBlank
    private String accessToken;

    @NotBlank
    private String whatsappId;
}
