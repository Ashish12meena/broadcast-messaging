package com.aigreentick.services.messaging.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.aigreentick.services.common.dto.CountryResponseDto;
import com.aigreentick.services.common.mapper.CountryMapper;
import com.aigreentick.services.common.model.Country;
import com.aigreentick.services.messaging.repository.CountryRepository;
import com.aigreentick.services.messaging.service.interfaces.CountryInterface;

@Service
public class CountryServiceImpl implements CountryInterface {

    private final CountryMapper countryMapper;
    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository, CountryMapper countryMapper) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
    }

    public List<CountryResponseDto> getAllCountries() {
        return countryRepository.findAll().stream()
                .filter(c -> c.getDeletedAt() == null)
                .map(countryMapper::toCountryDto)
                .collect(Collectors.toList());
    }

    public CountryResponseDto getCountryById(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id" + id));

        if (country.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Country is deleted");
        }

        return countryMapper.toCountryDto(country);
    }

    public CountryResponseDto getCountryByName(String name) {
        Country country = countryRepository.findByNameIgnoreCaseAndDeletedAtIsNull(name)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with name: " + name));
        return countryMapper.toCountryDto(country);
    }

    public CountryResponseDto getCountryByCode(String code) {
        Country country = countryRepository.findByMobileCodeAndDeletedAtIsNull(code)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with code: " + code));
        return countryMapper.toCountryDto(country);
    }

    public Long getCountryIdByMobileCode(String mobileCode){
        return countryRepository.findIdByMobileCodeAndDeletedAtIsNull(mobileCode)
          .orElseThrow(() -> new RuntimeException("Country not found by modile code"));
    }

    public void softDeleteCountry(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id" + id));

        country.setDeletedAt(LocalDateTime.now());
        countryRepository.save(country);
    }

}

