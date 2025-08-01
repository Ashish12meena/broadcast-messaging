package com.aigreentick.services.template.dto.buildTemplate.utility;

import lombok.Data;

@Data
public class UtilityButtonDto {
    private String type; // QUICK_REPLY, URL, PHONE_NUMBER
    private String text;
    private String url;
    private String phoneNumber;
}
