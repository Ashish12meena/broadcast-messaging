package com.aigreentick.services.messaging.dto.phonebook;

import java.util.Map;

import lombok.Data;

@Data
public class PhoneBookResponseDto {
    private String name;
    private String phoneNumber;
    Map<String,String> parameters;
}
