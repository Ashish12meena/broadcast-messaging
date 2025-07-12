package com.aigreentick.services.auth.dto.role;

import java.util.Set;

import com.aigreentick.services.auth.enums.RoleType;
import com.aigreentick.services.auth.model.Permission;

import lombok.Data;

@Data
public class RoleResponseDto {
    private Long id;
    private RoleType name;
    private Set<Permission> permissions;
}
