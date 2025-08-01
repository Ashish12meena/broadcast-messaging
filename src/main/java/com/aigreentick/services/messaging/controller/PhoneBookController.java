package com.aigreentick.services.messaging.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.auth.service.impl.CustomUserDetails;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.dto.ResponseStatus;
import com.aigreentick.services.messaging.dto.phonebook.PhoneBookEntryRequestDto;
import com.aigreentick.services.messaging.dto.phonebook.PhoneBookResponseDto;
import com.aigreentick.services.messaging.service.impl.PhoneBookEntryServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contacts")
public class PhoneBookController {
    private final PhoneBookEntryServiceImpl phoneBookEntryServiceImpl;

    @PostMapping("/add")
    public ResponseEntity<?> addContactInPhoneBook(@RequestBody PhoneBookEntryRequestDto request,
    @AuthenticationPrincipal CustomUserDetails loginUser ){
      PhoneBookResponseDto response =  phoneBookEntryServiceImpl.createPhoneBookEntry(request,loginUser.getId());
       return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage<>(ResponseStatus.SUCCESS.name(),"Contact added successfully",response));
    }
}
