package com.aigreentick.services.messaging.dto.group;

import java.util.List;

import com.aigreentick.services.messaging.model.group.GroupMembers;

import lombok.Data;

@Data
public class GroupResponseDto {
    private String groupName;
    private String createdBy;
    private Long groupId;
    private Integer totalMembers;
    List<GroupMembers> membersList;
}
