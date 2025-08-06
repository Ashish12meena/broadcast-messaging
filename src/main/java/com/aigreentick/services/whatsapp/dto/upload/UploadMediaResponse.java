package com.aigreentick.services.whatsapp.dto.upload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UploadMediaResponse {
    @JsonProperty("h")
    private String facebookImageUrl;
}