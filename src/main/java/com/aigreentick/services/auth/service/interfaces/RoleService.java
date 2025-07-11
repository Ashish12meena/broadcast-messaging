package com.aigreentick.services.auth.service.interfaces;

import java.util.List;

import com.aigreentick.services.auth.dto.RoleResponseDto;
import com.aigreentick.services.auth.dto.UserResponseDto;
import com.aigreentick.services.auth.enums.RoleType;

public interface RoleService {
     List<RoleResponseDto> getAllRoles();

     void addRole(String name, String description);

     void removeRole(String name);
     
     UserResponseDto addRoleToUser(Long userId, RoleType roleName);

     UserResponseDto removeRoleFromUser(Long userId, RoleType roleName);
}