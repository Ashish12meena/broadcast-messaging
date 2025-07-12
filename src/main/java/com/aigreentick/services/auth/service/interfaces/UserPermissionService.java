package com.aigreentick.services.auth.service.interfaces;

import java.util.List;

import com.aigreentick.services.auth.dto.permission.PermissionResponseDto;
import com.aigreentick.services.auth.dto.permission.UserPermissionResponseDto;

public interface UserPermissionService {
    void setUserPermission(Long userId, Long permissionId);
    UserPermissionResponseDto removeUserPermission(Long userId, Long permissionId);
    List<PermissionResponseDto> getUserPermissions(Long userId);
}
