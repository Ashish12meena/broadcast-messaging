package com.aigreentick.services.auth.dto.user;


import java.util.Set;

import com.aigreentick.services.auth.dto.role.RoleResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String username;
    private String mobileNumber;
    private String companyName;
    private String profileUrl;
    private Set<RoleResponseDto> role;
}

