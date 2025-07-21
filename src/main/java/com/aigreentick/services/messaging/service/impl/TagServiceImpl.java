package com.aigreentick.services.messaging.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.aigreentick.services.auth.exception.UserNotFoundException;
import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.service.interfaces.UserService;
import com.aigreentick.services.common.exception.ResourceAlreadyExistsException;
import com.aigreentick.services.common.exception.ResourceNotFoundException;
import com.aigreentick.services.messaging.dto.tag.TagRequestDto;
import com.aigreentick.services.messaging.dto.tag.TagResponseDto;
import com.aigreentick.services.messaging.enums.TagStatus;
import com.aigreentick.services.messaging.mapper.TagMapper;
import com.aigreentick.services.messaging.model.tag.Tag;
import com.aigreentick.services.messaging.model.tag.TagKeyword;
import com.aigreentick.services.messaging.model.tag.TagNumber;
import com.aigreentick.services.messaging.model.tag.TagSpecifications;
import com.aigreentick.services.messaging.repository.TagRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagServiceImpl {
    private final TagRepository tagRepository;
    private final UserService userService;
    private final TagMapper tagmapper;

    /**
     * Create or update a tag for a user with associated keywords.
     */
    @Transactional
    public TagResponseDto createTag(TagRequestDto tagDto, Long userId) {

        validateDuplicateTag(tagDto.getName(), userId);

        User user = getUser(userId);

        Tag tag = tagmapper.toEntity(tagDto, user);

        List<TagKeyword> keywordEntities = toKeywordEntities(tagDto.getKeywords(), tag);
        List<TagNumber> numberEntities = toNumberEntities(tagDto.getMobileNumbers(), tag);

        tag.setKeywords(keywordEntities);
        tag.setNumbers(numberEntities);

        tag = tagRepository.save(tag);
        return tagmapper.toDto(tag);
    }

    /**
     * Get tags of a user, with optional filters.
     */
    public List<TagResponseDto> getUserTags(Long userId, String search, LocalDate from, LocalDate to) {
        Specification<Tag> spec = (root, query, cb) -> cb.conjunction();

        spec = spec.and(TagSpecifications.userIdEquals(userId));

        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and(TagSpecifications.nameOrKeywordLike(search));
        }

        if (from != null) {
            spec = spec.and(TagSpecifications.createdAtAfter(from));
        }

        if (to != null) {
            spec = spec.and(TagSpecifications.createdAtBefore(to));
        }

        return tagRepository.findAll(spec).stream()
                .map(tagmapper::toDto)
                .collect(Collectors.toList());
    }

    public TagResponseDto getTagById(Long tagId, Long userId) {
        Tag tag = tagRepository.findByIdAndUserIdAndDeletedAtIsNull(tagId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        return tagmapper.toDto(tag);
    }

    public boolean existsByName(String name, Long userId) {
        return tagRepository.existsByUserIdAndName(userId, name);
    }

    @Transactional
    public void changeTagStatus(Long tagId, TagStatus status, Long userId) {
        Tag tag = tagRepository.findByIdAndUserIdAndDeletedAtIsNull(tagId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        tag.setStatus(status);
        tag.setUpdatedAt(LocalDateTime.now());
        tagRepository.save(tag);
    }

    //
    public List<TagResponseDto> getTagsWithKeyword(Long userId, String keyword) {
        return tagRepository.findTagsByKeyword(userId, keyword).stream()
                .map(tagmapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TagResponseDto> getTagsLinkedToNumber(Long userId, String phoneNumber) {

        return tagRepository.findTagsByNumber(userId, phoneNumber).stream()
                .map(tagmapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void permanentlyDeleteSoftDeletedTagsOlderThan(int days) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        List<Tag> oldSoftDeleted = tagRepository.findAllByDeletedAtBefore(threshold);
        tagRepository.deleteAll(oldSoftDeleted);
    }

    public long countTagsForUser(Long userId) {
        return tagRepository.countByUserIdAndDeletedAtIsNull(userId);
    }

    // Private Helper methods

    private void validateDuplicateTag(String name, Long userId) {
        if (existsByName(name, userId)) {
            throw new ResourceAlreadyExistsException("Tag already exists");
        }
    }

    private User getUser(Long userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private List<TagKeyword> toKeywordEntities(List<String> keywords, Tag tag) {
        return (keywords == null ? Collections.emptyList() : keywords)
                .stream()
                .map(k -> TagKeyword.builder().name((String) k).tag(tag).build())
                .collect(Collectors.toList());
    }

    private List<TagNumber> toNumberEntities(List<String> numbers, Tag tag) {
        return (numbers == null ? Collections.emptyList() : numbers)
                .stream()
                .map(n -> TagNumber.builder().number((String) n).tag(tag).build())
                .collect(Collectors.toList());
    }

}
