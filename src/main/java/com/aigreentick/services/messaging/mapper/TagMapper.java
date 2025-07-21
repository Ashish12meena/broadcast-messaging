package com.aigreentick.services.messaging.mapper;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.messaging.dto.tag.TagRequestDto;
import com.aigreentick.services.messaging.dto.tag.TagResponseDto;
import com.aigreentick.services.messaging.enums.TagStatus;
import com.aigreentick.services.messaging.model.tag.Tag;
import com.aigreentick.services.messaging.model.tag.TagKeyword;
import com.aigreentick.services.messaging.model.tag.TagNumber;

@Component
public class TagMapper {
     public TagResponseDto toDto(Tag tag) {
        return TagResponseDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getTagColor())
                .status(tag.getStatus())
                .keywords(tag.getKeywords().stream().map(TagKeyword::getName).collect(Collectors.toList()))
                .mobileNumbers(tag.getNumbers().stream().map(TagNumber::getNumber).collect(Collectors.toList()))
                .createdAt(tag.getCreatedAt())
                .build();
    }

     public Tag toEntity(TagRequestDto tagDto,User user) {
       return Tag.builder()
       .user(user)
       .name(tagDto.getName())
       .tagColor(tagDto.getColor())
       .status(TagStatus.ACTIVE)
       .createdAt(LocalDateTime.now())
       .updatedAt(LocalDateTime.now())
        .build();
     }
}
