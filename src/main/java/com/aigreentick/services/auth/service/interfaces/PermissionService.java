package com.aigreentick.services.auth.service.interfaces;

import java.util.List;

import com.aigreentick.services.auth.dto.permission.PermissionResponseDto;

public interface PermissionService {
     PermissionResponseDto addPermission(String name, String description);
     PermissionResponseDto removePermission(String name);
     List<PermissionResponseDto> getAllPermissions();
}
