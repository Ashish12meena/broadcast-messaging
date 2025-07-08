package com.aigreentick.services.auth.dto;

import java.util.List;

import com.aigreentick.services.auth.enums.RoleType;
import com.aigreentick.services.auth.model.Permission;

import lombok.Data;

@Data
public class RoleResponseDto {
    private Long id;
    private RoleType name;
    private List<Permission> permissions;
}
