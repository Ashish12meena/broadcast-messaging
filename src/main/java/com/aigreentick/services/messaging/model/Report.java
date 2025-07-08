package com.aigreentick.services.messaging.model;


import java.time.Instant;
import java.util.Map;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reports")
public class Report {
    @Id
    private String id;

    @Indexed
    private Long userId;

    @Indexed
    private Long broadcastId;

    @Indexed
    private Long campaignId;

    @Indexed
    private Long groupSendId;

    @Indexed
    private Long tagLogId;

    private String mobile;

    private String type = "template";

    @Indexed
    private String messageId;

    private String waId;

    private String messageStatus;

    private String status;

    private Map<String, Object> response;

    private Map<String, Object> contact;

    private ReportPlatform platform = ReportPlatform.WEB;

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    private Instant deletedAt;

    private enum ReportPlatform {
        API,
        WEB
    }

}
