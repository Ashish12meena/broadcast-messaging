package com.aigreentick.services.messaging.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CountryResponseDto {
    private Long id;
    private String name;
    private String mobileCode;
}
