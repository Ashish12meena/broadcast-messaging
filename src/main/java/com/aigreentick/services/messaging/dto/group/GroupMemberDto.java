package com.aigreentick.services.messaging.dto.group;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupMemberDto {
    private Long id;
    private Long addedBy;
    private Long userId;
}
