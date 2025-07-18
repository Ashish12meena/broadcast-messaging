package com.aigreentick.services.messaging.dto.template;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacebookApiCredentialsDto {
      @NotBlank(message = "API version must not be blank")
    private String apiVersion;

    @NotBlank(message = "Access token must not be blank")
    private String accessToken;

    @NotBlank(message = "WhatsApp ID must not be blank")
    private String whatsappId;
}
