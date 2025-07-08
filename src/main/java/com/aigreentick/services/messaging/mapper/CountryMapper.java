package com.aigreentick.services.messaging.mapper;

import org.springframework.stereotype.Component;

import com.aigreentick.services.messaging.dto.CountryResponseDto;
import com.aigreentick.services.messaging.model.Country;

@Component
public class CountryMapper {


    public CountryResponseDto toCountryDto(Country country) {
        return new CountryResponseDto(
                country.getId(),
                country.getName(),
                country.getMobileCode()
        );
    }
}
