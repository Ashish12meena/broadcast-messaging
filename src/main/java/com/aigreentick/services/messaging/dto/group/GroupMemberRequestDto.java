package com.aigreentick.services.messaging.dto.group;

import lombok.Data;

@Data
public class GroupMemberRequestDto {
    private Long groupId;
     private Long memberUserId;
}
