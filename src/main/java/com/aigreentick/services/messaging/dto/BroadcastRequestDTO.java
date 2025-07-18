package com.aigreentick.services.messaging.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Future;
import lombok.Data;

@Data
public class BroadcastRequestDTO {
    private Long temlateId;
    private String campanyName;
    private Long countryId;
    private List<String> mobileNumbers;
    private boolean isMedia;
    private String mediaUrl;
    private String mediaType;

    @Future(message = "Schedule date must be in the future")
    private LocalDateTime scheduledAt;    
}
