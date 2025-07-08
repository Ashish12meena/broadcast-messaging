package com.aigreentick.services.auth.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.aigreentick.services.auth.dto.RoleResponseDto;
import com.aigreentick.services.auth.model.Role;

@Component
public class RoleMapper {
     public RoleResponseDto toRoleResponseDto(Role role) {
        RoleResponseDto dto = new RoleResponseDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setPermissions(role.getPermissions());
        return dto;
    }

    public List<RoleResponseDto> toRoleResponseDtoList(List<Role> roles) {
        return roles.stream()
                    .map(this::toRoleResponseDto)
                    .collect(Collectors.toList());
    }
}
