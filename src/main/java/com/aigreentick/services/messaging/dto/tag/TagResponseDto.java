package com.aigreentick.services.messaging.dto.tag;

import java.time.LocalDateTime;
import java.util.List;

import com.aigreentick.services.messaging.enums.TagStatus;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class TagResponseDto {
    private Long id;
    private String name;
    private String color;
    private TagStatus status;
    private List<String> keywords;
    private List<String> mobileNumbers;
    private LocalDateTime createdAt;
}
