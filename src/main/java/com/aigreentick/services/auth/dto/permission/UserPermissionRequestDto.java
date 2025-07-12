package com.aigreentick.services.auth.dto.permission;

import lombok.Data;

@Data
public class UserPermissionRequestDto {
    private Long userId;
    private Long permissionId;
}
