package com.aigreentick.services.messaging.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMemberRemoveResponseDto {
    private String removedBy;
    private String memeber;
}
