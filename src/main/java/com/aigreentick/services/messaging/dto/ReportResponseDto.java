package com.aigreentick.services.messaging.dto;


import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDto {
    private Long broadCastId;
    private Long campaignId;
    private String mobile;
    private String status;
    private String messageId;
    private String waId;
    private String messageStatus;
    private Map<String, Object> response;
}
