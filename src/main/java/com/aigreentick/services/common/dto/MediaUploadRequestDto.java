package com.aigreentick.services.common.dto;

import lombok.Data;

@Data
public class MediaUploadRequestDto {
    private String wabaAppId;
    private String accessToken;
    private String apiVersion="v23.0";
}
