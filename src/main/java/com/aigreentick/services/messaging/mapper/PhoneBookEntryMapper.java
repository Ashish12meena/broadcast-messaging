package com.aigreentick.services.messaging.mapper;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.messaging.dto.phonebook.PhoneBookEntryRequestDto;
import com.aigreentick.services.messaging.dto.phonebook.PhoneBookResponseDto;
import com.aigreentick.services.messaging.model.PhoneBookEntry;

@Component
public class PhoneBookEntryMapper {
    public PhoneBookEntry toEntity(PhoneBookEntryRequestDto dto,String phoneNumber, User user) {
        PhoneBookEntry entry = new PhoneBookEntry();
        entry.setPhoneNumber(phoneNumber);
        entry.setUser(user);
        entry.setName(dto.getName());
        entry.setParametersJson(dto.getParameters() != null ? dto.getParameters() : new HashMap<>());
        entry.setCreatedAt(LocalDateTime.now());
        return entry;
    }

    public PhoneBookResponseDto toDto(PhoneBookEntry entry) {
        PhoneBookResponseDto dto = new PhoneBookResponseDto();
        dto.setName(entry.getName());
        dto.setPhoneNumber(entry.getPhoneNumber());
        dto.setParameters(entry.getParametersJson());
        return dto;
    }
}
