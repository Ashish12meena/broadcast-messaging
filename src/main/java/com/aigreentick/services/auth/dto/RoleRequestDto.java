package com.aigreentick.services.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RoleRequestDto {

    @NotBlank(message = "Role name is required")
    @Pattern(regexp = "^ROLE_[A-Z_]+$", message = "Role name must start with 'ROLE_' and use uppercase letters")
    private String name;

    private String description;
}
