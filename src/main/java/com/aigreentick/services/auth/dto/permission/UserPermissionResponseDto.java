package com.aigreentick.services.auth.dto.permission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPermissionResponseDto {
    private Long userId;
    private Long permissionId;
    private String permissionName;
}
