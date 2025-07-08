package com.aigreentick.services.auth.dto;

import com.aigreentick.services.auth.enums.RoleType;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String mobileNumber;
    private RoleType role;
}
