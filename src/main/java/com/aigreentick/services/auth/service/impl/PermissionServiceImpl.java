package com.aigreentick.services.auth.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aigreentick.services.auth.dto.permission.PermissionResponseDto;
import com.aigreentick.services.auth.exception.PermissionAlreadyExistsException;
import com.aigreentick.services.auth.exception.PermissionNotFoundException;
import com.aigreentick.services.auth.mapper.PermissionMapper;
import com.aigreentick.services.auth.model.Permission;
import com.aigreentick.services.auth.repository.PermissionRepostory;
import com.aigreentick.services.auth.service.interfaces.PermissionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepostory permissionRepostory;
    private final PermissionMapper permissionMapper;

    public Optional<Permission> findById(Long permissionId) {
        return permissionRepostory.findById(permissionId);
    }

    /**
     * Adds a new permission if it doesn't already exist.
     */
    public PermissionResponseDto addPermission(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Permission name must not be null or blank.");
        }

        String normalizedName = name.trim().toUpperCase();

        if (permissionRepostory.findByName(normalizedName).isPresent()) {
            throw new PermissionAlreadyExistsException(normalizedName);
        }

        Permission permission = new Permission();
        permission.setName(normalizedName);
        permission.setDescription(description);

        Permission savedPermission = permissionRepostory.save(permission);
        return permissionMapper.toDto(savedPermission);
    }

    public PermissionResponseDto removePermission(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Permission name must not be null or blank.");
        }

        String normalizedName = name.trim().toUpperCase();

        Permission permission = permissionRepostory.findByName(normalizedName)
                .orElseThrow(() -> new PermissionNotFoundException(normalizedName));

        permissionRepostory.delete(permission);

        return permissionMapper.toDto(permission);
    }

    public List<PermissionResponseDto> getAllPermissions() {
       List<Permission> permissions = permissionRepostory.findAll();
       return permissions.stream()
               .map(permissionMapper::toDto)
               .toList();
    }

}
