package com.aigreentick.services.messaging.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aigreentick.services.common.exception.ResourceNotFoundException;
import com.aigreentick.services.messaging.dto.tag.TagNumberRequestDto;
import com.aigreentick.services.messaging.dto.tag.TagNumberResponseDto;
import com.aigreentick.services.messaging.mapper.TagNumberMapper;
import com.aigreentick.services.messaging.model.tag.Tag;
import com.aigreentick.services.messaging.model.tag.TagNumber;
import com.aigreentick.services.messaging.repository.TagNumberRepository;
import com.aigreentick.services.messaging.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagNumberServiceImpl {
    private final TagNumberRepository tagNumberRepository;
    private final TagRepository tagRepository;
    private final TagNumberMapper tagNumberMapper;

    public TagNumberResponseDto createTagNumber(TagNumberRequestDto request) {
        Tag tag = tagRepository.findById(request.getTagId())
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + request.getTagId()));

        TagNumber tagNumber = TagNumber.builder()
                .number(request.getNumber())
                .tag(tag)
                .build();

        return tagNumberMapper.toDto(tagNumberRepository.save(tagNumber));
    }

    public List<TagNumberResponseDto> getNumbersByTag(Long tagId) {
        return tagNumberRepository.findByTagId(tagId).stream()
                .map(tagNumberMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteNumber(Long numberId) {
        if (!tagNumberRepository.existsById(numberId)) {
            throw new ResourceNotFoundException("Tag number not found with id: " + numberId);
        }
        tagNumberRepository.deleteById(numberId);
    }

    @Transactional
    public void deleteNumberFromAllTags(Long userId, String number) {
        tagNumberRepository.deleteByNumberAndUserId(number, userId);
    }

}
