package com.aigreentick.services.messaging.service.impl;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aigreentick.services.common.exception.ResourceNotFoundException;
import com.aigreentick.services.messaging.dto.tag.TagKeywordRequestDto;
import com.aigreentick.services.messaging.dto.tag.TagKeywordResponseDto;
import com.aigreentick.services.messaging.mapper.TagKeywordMapper;
import com.aigreentick.services.messaging.model.tag.Tag;
import com.aigreentick.services.messaging.model.tag.TagKeyword;
import com.aigreentick.services.messaging.repository.TagKeywordRepository;
import com.aigreentick.services.messaging.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagKeywordServiceImpl {
    private final TagKeywordRepository tagKeywordRepository;
    private final TagRepository tagRepository;
    private final TagKeywordMapper tagKeywordMapper;

    public TagKeywordResponseDto createKeyword(TagKeywordRequestDto request) {
        Tag tag = tagRepository.findById(request.getTagId())
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + request.getTagId()));

        TagKeyword keyword = TagKeyword.builder()
                .name(request.getName())
                .tag(tag)
                .build();

        TagKeyword saved = tagKeywordRepository.save(keyword);
        return tagKeywordMapper.toDto(saved);
    }

    public List<TagKeywordResponseDto> getKeywordsByTag(Long tagId) {
        return tagKeywordRepository.findByTagIdAndDeletedAtIsNull(tagId).stream()
                .map(tagKeywordMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteKeyword(Long keywordId) {
    if (!tagKeywordRepository.existsById(keywordId)) {
        throw new ResourceNotFoundException("Keyword not found with id: " + keywordId);
    }

    tagKeywordRepository.deleteById(keywordId);
}
}
