package com.aigreentick.services.messaging.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.aigreentick.services.messaging.dto.group.GroupRequestDto;
import com.aigreentick.services.messaging.dto.group.GroupResponseDto;
import com.aigreentick.services.messaging.dto.group.GroupResponseForUserDto;
import com.aigreentick.services.messaging.model.group.Group;

@Component
public class GroupMapper {

    public Group toGroupEntity(GroupRequestDto dto) {
        Group group = new Group();
        group.setGroupName(dto.getGroupName());
        group.setCreatedAt(LocalDateTime.now());
        group.setCreatedAt(LocalDateTime.now());
        return group;
    }

    public GroupResponseDto toGroupResponseDto(Group group) {
        GroupResponseDto response = new GroupResponseDto();
        response.setGroupName(group.getGroupName());
        response.setCreatedBy(group.getOwner().getUsername());
        response.setGroupId(group.getId());
        response.setTotalMembers(group.getMembers() != null ? group.getMembers().size() : 0);
        response.setMembersList(group.getMembers() != null ? group.getMembers() : new ArrayList<>());
        return response;
    }

    public GroupResponseForUserDto toGroupResponseForUserDto(Group group) {
        GroupResponseForUserDto response = new GroupResponseForUserDto();
        response.setGroupId(group.getId());
        response.setGroupName(group.getGroupName());
        return response;
    }
}
