package com.aigreentick.services.messaging.dto.phonebook;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PhoneBookEntryRequestDto {

    private String name;

    @NotNull(message = "Phone Number Cannot be blank")
    private String phoneNumber;

    @NotNull(message = "Please enter country code first")
    private String countryCode;
    
    Map<String,String> parameters;
}
