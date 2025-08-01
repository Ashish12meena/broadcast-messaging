package com.aigreentick.services.template.dto.buildTemplate.marketing;

import lombok.Data;

@Data
public class MarketingButtonDto {
    private String type; // QUICK_REPLY, URL, PHONE_NUMBER
    private String text;
    private String url;          // if type is URL
    private String phoneNumber;  // if type is PHONE_NUMBER
}
