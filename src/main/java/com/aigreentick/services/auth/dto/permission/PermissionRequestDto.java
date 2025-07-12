package com.aigreentick.services.auth.dto.permission;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRequestDto {

    @NotBlank(message = "Permission name cannot be blank")
     private String name;
     private String description;
}
