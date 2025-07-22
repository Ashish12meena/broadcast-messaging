package com.aigreentick.services.messaging.dto.tag;

import java.util.List;

import lombok.Data;

@Data
public class TagRequestDto {
    private String name;
    private String color;
    private List<String> keywords;
    private List<String> mobileNumbers;
    private long countryId;
}
