package com.aigreentick.services.auth.dto;

import lombok.Data;

@Data
public class UserPermissionRequestDto {
    private Long userId;
    private Long permissionId;
}
