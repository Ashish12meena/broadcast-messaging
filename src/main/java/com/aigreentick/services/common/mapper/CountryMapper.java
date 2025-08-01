package com.aigreentick.services.common.mapper;

import org.springframework.stereotype.Component;

import com.aigreentick.services.common.dto.CountryResponseDto;
import com.aigreentick.services.common.model.Country;

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
