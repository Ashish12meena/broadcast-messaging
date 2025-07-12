package com.aigreentick.services.auth.mapper;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.aigreentick.services.auth.dto.RegisterRequest;
import com.aigreentick.services.auth.dto.user.UserResponseDto;
import com.aigreentick.services.auth.model.User;

@Component
public class UserMapper {
    private final BCryptPasswordEncoder encoder;
    private final RoleMapper roleMapper;

    
    public UserMapper(BCryptPasswordEncoder encoder, RoleMapper roleMapper) {
        this.encoder = encoder;
        this.roleMapper= roleMapper;
    }
    public User toUser(RegisterRequest userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setMobileNumber(userDto.getMobileNumber());
        user.setPassword(encoder.encode(userDto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    public UserResponseDto toUserResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setCompanyName(user.getCompanyName());
        dto.setProfileUrl(user.getProfilePhoto());
        if (user.getRoles()!=null) {
            dto.setRole(roleMapper.toRoleResponseDtoSet(user.getRoles()));
        }
        return dto;
    }

}
