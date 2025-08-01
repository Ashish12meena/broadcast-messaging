package com.aigreentick.services.messaging.service.interfaces;

import java.util.List;

import com.aigreentick.services.common.dto.CountryResponseDto;

public interface CountryInterface {
    List<CountryResponseDto> getAllCountries();
}