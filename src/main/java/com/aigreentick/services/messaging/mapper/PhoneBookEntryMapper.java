package com.aigreentick.services.messaging.mapper;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.aigreentick.services.messaging.dto.phonebook.PhoneBookEntryRequestDto;
import com.aigreentick.services.messaging.model.PhoneBookEntry;
import com.aigreentick.services.template.model.Template;

@Component
public class PhoneBookEntryMapper {
    public PhoneBookEntry toEntity(PhoneBookEntryRequestDto dto, Template template) {
        PhoneBookEntry entry = new PhoneBookEntry();
        entry.setPhoneNumber(dto.getPhoneNumber());
        entry.setTemplate(template);
        entry.setParametersJson(dto.getCountryCode() != null ? dto.getParameters() : new HashMap<>());
        entry.setCreatedAt(LocalDateTime.now());
        return entry;
    }
}
