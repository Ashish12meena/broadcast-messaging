package com.aigreentick.services.messaging.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aigreentick.services.auth.exception.UserNotFoundException;
import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.service.interfaces.UserService;
import com.aigreentick.services.messaging.dto.group.GroupRequestDto;
import com.aigreentick.services.messaging.dto.group.GroupResponseDto;
import com.aigreentick.services.messaging.dto.group.GroupResponseForUserDto;
import com.aigreentick.services.messaging.mapper.GroupMapper;
import com.aigreentick.services.messaging.model.group.Group;
import com.aigreentick.services.messaging.repository.GroupRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupServiceImpl {
    private final GroupRepository groupRepository;
    private final UserService userService;
    private final GroupMapper groupMapper;

    /**
     * Creates a new group for a given user ID.
     */
    public GroupResponseDto createGroup(GroupRequestDto groupRequestDto, Long userId) {
        if (groupRepository.existsByGroupNameAndOwner_Id(groupRequestDto.getGroupName(), userId)) {
            throw new IllegalArgumentException("Group name already exists for this user");
        }

        User user = userService.findById(userId).orElseThrow(()->  new UserNotFoundException("User Not Found with id "+userId));
        Group group = groupMapper.toGroupEntity(groupRequestDto);
        group.setOwner(user);

        Group savedGroup = groupRepository.save(group);
        log.info("Group '{}' created successfully by user ID '{}'", savedGroup.getGroupName(), userId);

        return groupMapper.toGroupResponseDto(savedGroup);
    }

    /**
     * Returns a group by its ID.
     */
    public Group getGroupById(Long groupId) {
        return groupRepository.findByIdAndDeletedAtIsNull(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + groupId));
    }

    /**
     * Returns all groups owned by a user.
     */
    public List<GroupResponseForUserDto> getUserGroups(Long userId) {
        return groupRepository.findAllByOwner_IdAndDeletedAtIsNull(userId).stream()
                .map(groupMapper::toGroupResponseForUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Soft deletes a group by marking deletedAt timestamp.
     */
    @Transactional
    public boolean deleteGroup(Long groupId, Long ownerId) {
        Group group = groupRepository.findByIdAndOwner_IdAndDeletedAtIsNull(groupId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found or already deleted"));

        group.setDeletedAt(LocalDateTime.now());
        return true;
    }

    /**
     * Updates a group's name. Ensures the new name is not a duplicate for this user.
     */
    @Transactional
    public GroupResponseDto updateGroupName(Long groupId, String newName, Long userId) {
        Group group = groupRepository.findByIdAndOwner_IdAndDeletedAtIsNull(groupId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found or not owned by user"));

        if (groupRepository.existsByGroupNameAndOwner_Id(newName, userId)) {
            throw new IllegalArgumentException("Group name already exists for this user");
        }

        group.setGroupName(newName);
        group.setUpdatedAt(LocalDateTime.now());
        return groupMapper.toGroupResponseDto(group);
    }

    
}
