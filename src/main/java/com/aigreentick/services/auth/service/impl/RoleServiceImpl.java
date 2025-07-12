package com.aigreentick.services.auth.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.kafka.common.errors.DuplicateResourceException;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.stereotype.Service;

import com.aigreentick.services.auth.dto.role.RoleDto;
import com.aigreentick.services.auth.dto.role.RoleResponseDto;
import com.aigreentick.services.auth.dto.user.UserResponseDto;
import com.aigreentick.services.auth.enums.RoleType;
import com.aigreentick.services.auth.exception.RoleNotFoundException;
import com.aigreentick.services.auth.exception.UserNotFoundException;
import com.aigreentick.services.auth.mapper.RoleMapper;
import com.aigreentick.services.auth.mapper.UserMapper;
import com.aigreentick.services.auth.model.Role;
import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.repository.RoleRepository;
import com.aigreentick.services.auth.repository.UserRepository;
import com.aigreentick.services.auth.service.interfaces.RoleService;
import com.aigreentick.services.common.exception.RoleAlreadyAssignedException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<RoleResponseDto> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roleMapper.toRoleResponseDtoList(roles);
    }

    @Override
    public void addRole(String name, String description) {
        log.info("Attempting to add new role: {}", name);

        RoleType roleType;
        try {
            roleType = RoleType.valueOf(name);
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid role name provided for removal: {}", name);
            throw new InvalidRequestException("Invalid role name: " + name);
        }

        // Check for duplicate
        boolean exists = roleRepository.existsByName(roleType);
        if (exists) {
            log.warn("Role creation failed: Role with name '{}' already exists", name);
            throw new DuplicateResourceException("Role already exists with name: " + name);
        }

        try {
            Role role = new Role();
            role.setName(roleType); // Enum, not String
            role.setDescription(description);
            roleRepository.save(role);

            log.info("Role '{}' created successfully", name);
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid role name provided: {}", name);
            throw new InvalidRequestException("Invalid role name: " + name);
        }
    }

    @Override
    public void removeRole(String name) {
        log.info("Attempting to remove role: {}", name);

        RoleType roleType;
        try {
            roleType = RoleType.valueOf(name);
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid role name provided for removal: {}", name);
            throw new InvalidRequestException("Invalid role name: " + name);
        }

        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleType));

        if (role.getUsers().isEmpty()) {
            roleRepository.delete(role);
            log.info("Role '{}' removed successfully", name);
        } else {
            log.warn("Role '{}' cannot be removed as it is assigned to users", name);
            throw new RoleAlreadyAssignedException("Role cannot be removed as it is assigned to users");
        }
    }

    @Override
    public UserResponseDto addRoleToUser(Long userId, RoleType roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));

        if (user.getRoles().contains(role)) {
            throw new RoleAlreadyAssignedException("User already has role: " + roleName);
        }
        user.getRoles().add(role);
        userRepository.save(user);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto removeRoleFromUser(Long userId, RoleType roleType) {
        // 1. Fetch user with roles eagerly
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        // 2. Fetch managed Role from DB (needed for identity equality)
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleType));

        Set<Role> newRoles = new HashSet<>(user.getRoles());
        newRoles.remove(role);
        user.setRoles(newRoles);

        // 4. Save not required if @Transactional, but safe
        userRepository.save(user);
        return userMapper.toUserResponseDto(user);
        
    }

    @Override
    public List<RoleDto> getAllRolesForUser(Long userId) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (user.getRoles().isEmpty()) {
            log.warn("User with ID {} has no roles assigned", userId);
            throw new RoleNotFoundException("No roles assigned to user with ID: " + userId);
        }

        return user.getRoles().stream()
                .map(role -> new RoleDto(role.getId(), role.getName().name()))
                .toList();
    }

}
