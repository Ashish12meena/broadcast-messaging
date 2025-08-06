package com.aigreentick.services.common.dto;

import com.aigreentick.services.common.model.Media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadResponseDto {
    private String fileName;
    private long fileSize;
    private String mimeType;
    private String mediaUrl;
    private String sessionId;

    
}
