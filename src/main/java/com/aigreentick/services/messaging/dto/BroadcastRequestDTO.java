package com.aigreentick.services.messaging.dto;

import java.util.List;

import lombok.Data;

@Data
public class BroadcastRequestDTO {
    private String template;
    private Long templateId;
    private String campName;
    private String country;
    private Long countryId;
    private List<String> mobileNumbers;
    private boolean isMedia;
    private String mediaUrl;
    private String mediaType;
    private String scheduleDate;    

}
