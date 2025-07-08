package com.aigreentick.services.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String username;
    private String email;
    private String password;  
    private String role;  
    private String mobileNumber;

     public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber != null ? mobileNumber.trim() : null;
    }
}
