package com.aigreentick.services.common.mapper;

import org.springframework.stereotype.Component;

import com.aigreentick.services.common.dto.MediaUploadResponseDto;
import com.aigreentick.services.common.model.Media;
import com.aigreentick.services.common.util.FileMetaData;

@Component
public class MediaMapper {
    public MediaUploadResponseDto toDto(Media media) {
        return MediaUploadResponseDto.builder()
                .fileName(media.getFileName())
                .fileSize(media.getFileSize())
                .mimeType(media.getMimeType())
                .mediaUrl(media.getMediaHandle())
                .sessionId(media.getSessionId())
                .build();
    }

     public  Media toEntity(Long userId, String sessionId, FileMetaData meta, String handle) {
        return Media.builder()
                .fileName(meta.getFileName())
                .fileSize(meta.getFileSize())
                .mimeType(meta.getMimeType())
                .mediaHandle(handle)
                .sessionId(sessionId)
                .build();
        
    }
}
