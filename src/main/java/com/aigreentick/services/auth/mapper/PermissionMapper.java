package com.aigreentick.services.auth.mapper;

import org.springframework.stereotype.Component;

import com.aigreentick.services.auth.dto.permission.PermissionRequestDto;
import com.aigreentick.services.auth.dto.permission.PermissionResponseDto;
import com.aigreentick.services.auth.model.Permission;

@Component
public class PermissionMapper {

    public Permission toEntity(PermissionRequestDto dto) {
        Permission permission = new Permission();
        permission.setName(dto.getName());
        permission.setDescription(dto.getDescription());
        return permission;
    }

    public PermissionResponseDto toDto(Permission entity) {
        PermissionResponseDto dto = new PermissionResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        return dto;
    }
}
