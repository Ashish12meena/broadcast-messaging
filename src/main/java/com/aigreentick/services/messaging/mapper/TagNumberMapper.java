package com.aigreentick.services.messaging.mapper;

import org.springframework.stereotype.Component;

import com.aigreentick.services.messaging.dto.tag.TagNumberResponseDto;
import com.aigreentick.services.messaging.model.tag.TagNumber;

@Component
public class TagNumberMapper {
    public TagNumberResponseDto toDto(TagNumber entity) {
        TagNumberResponseDto dto = new TagNumberResponseDto();
        dto.setId(entity.getId());
        dto.setNumber(entity.getNumber());
        dto.setTagId(entity.getTag().getId());
        return dto;
    }
}