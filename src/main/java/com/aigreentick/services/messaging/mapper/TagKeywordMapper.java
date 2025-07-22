package com.aigreentick.services.messaging.mapper;

import org.springframework.stereotype.Component;

import com.aigreentick.services.messaging.dto.tag.TagKeywordResponseDto;
import com.aigreentick.services.messaging.model.tag.TagKeyword;

@Component
public class TagKeywordMapper {
     public TagKeywordResponseDto toDto(TagKeyword entity) {
        TagKeywordResponseDto dto = new TagKeywordResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setTagId(entity.getTag().getId());
        return dto;
    }
}
