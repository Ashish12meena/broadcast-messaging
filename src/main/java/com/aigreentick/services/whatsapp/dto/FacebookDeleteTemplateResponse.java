package com.aigreentick.services.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookDeleteTemplateResponse {
    private boolean success;
}