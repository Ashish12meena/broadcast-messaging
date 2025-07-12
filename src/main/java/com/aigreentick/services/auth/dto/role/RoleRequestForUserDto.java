package com.aigreentick.services.auth.dto.role;

import com.aigreentick.services.auth.enums.RoleType;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequestForUserDto {
    @NotBlank(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Role name is required")
    private RoleType roleName;
}
