package com.aigreentick.services.common.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.common.dto.CountryResponseDto;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.service.interfaces.CountryInterface;

@RestController
@RequestMapping("/api/countries")
public class CountryController {
    private final CountryInterface countryService;

    public CountryController(CountryInterface countryServiceInterface){
        this.countryService = countryServiceInterface;
    }
    

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllCountries(){
       List<CountryResponseDto>  countryResponseDto = countryService.getAllCountries();
       return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage<>("Success","List of Countries",countryResponseDto));
    }
}
