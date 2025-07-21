package com.aigreentick.services.messaging.mapper;

import org.springframework.stereotype.Component;

import com.aigreentick.services.messaging.dto.group.GroupMemberDto;
import com.aigreentick.services.messaging.model.group.GroupMembers;

@Component
public class GroupMembersMapper {
    public GroupMemberDto toGroupMemberDto(GroupMembers savedMember) {
        return GroupMemberDto.builder()
                .addedBy(savedMember.getAddedBy().getId())
                .id(savedMember.getId())
                .userId(savedMember.getMemberUser().getId())
                .build();
    }

}
