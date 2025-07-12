package com.aigreentick.services.auth.service.interfaces;

import java.util.List;

import com.aigreentick.services.auth.dto.role.RoleDto;
import com.aigreentick.services.auth.dto.role.RoleResponseDto;
import com.aigreentick.services.auth.dto.user.UserResponseDto;
import com.aigreentick.services.auth.enums.RoleType;

public interface RoleService {
     List<RoleResponseDto> getAllRoles();

     void addRole(String name, String description);

     void removeRole(String name);
     
     UserResponseDto addRoleToUser(Long userId, RoleType roleName);

     UserResponseDto removeRoleFromUser(Long userId, RoleType roleName);

     List<RoleDto> getAllRolesForUser(Long userId);
}