package com.aigreentick.services.messaging.dto.report;


import java.util.Map;

import com.aigreentick.services.common.enums.MessageStatusEnum;
import com.aigreentick.services.messaging.enums.ReportStatus;

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
    private ReportStatus status;
    private String messageId;
    private String waId;
    private MessageStatusEnum messageStatus;
    private Map<String, Object> response;
}
