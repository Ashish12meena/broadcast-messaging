package com.aigreentick.services.auth.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aigreentick.services.auth.dto.permission.PermissionResponseDto;
import com.aigreentick.services.auth.dto.permission.UserPermissionResponseDto;
import com.aigreentick.services.auth.exception.UserNotFoundException;
import com.aigreentick.services.auth.model.Permission;
import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.model.UserPermission;
import com.aigreentick.services.auth.repository.UserPermissionRepository;
import com.aigreentick.services.auth.service.interfaces.UserPermissionService;
import com.aigreentick.services.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserPermissionServiceImpl implements UserPermissionService {
    private final UserServiceImpl userService;
    private final PermissionServiceImpl permissionService;
    private final UserPermissionRepository userPermissionRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserPermissionServiceImpl.class);

    public void setUserPermission(Long userId, Long permissionId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Permission permission = permissionService.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with ID: " + permissionId));

        // Check if the permission is already assigned
        boolean alreadyHasPermission = user.getUserPermissions().stream()
                .anyMatch(up -> up.getPermission().getId().equals(permissionId));

                
        if (alreadyHasPermission) {
            logger.info("User {} already has permission {}", userId, permission.getName());
            return;
        }

        // Create and add new permission
        UserPermission userPermission = new UserPermission();
        userPermission.setUser(user);
        userPermission.setPermission(permission);
        userPermission.setCreatedAt(LocalDateTime.now());

        user.getUserPermissions().add(userPermission); // Maintain bi-directional integrity if needed
        userPermissionRepository.save(userPermission); // Save explicitly if cascade not used

        logger.info("Assigned permission {} to user {}", permission.getName(), userId);
    }

    public UserPermissionResponseDto removeUserPermission(Long userId, Long permissionId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Permission permission = permissionService.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with ID: " + permissionId));

        UserPermission userPermissionToRemove = user.getUserPermissions().stream()
                .filter(up -> up.getPermission().getId().equals(permissionId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User does not have the given permission"));

        user.getUserPermissions().remove(userPermissionToRemove);

        userPermissionRepository.delete(userPermissionToRemove);

        logger.info("Removed permission {} from user {}", permission.getName(), userId);

        return new UserPermissionResponseDto(userId,permissionId,permission.getName());

    }

    public List<PermissionResponseDto> getUserPermissions(Long userId) {
          List<UserPermission> userPermissions = userPermissionRepository.findByUserIdAndGrantedTrue(userId);
          return userPermissions.stream().map(
                up -> new PermissionResponseDto(up.getPermission().getId(), up.getPermission().getName(), up.getPermission().getDescription())
          ).toList();
    }

}
