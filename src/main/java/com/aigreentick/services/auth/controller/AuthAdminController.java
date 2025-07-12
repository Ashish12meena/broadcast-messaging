package com.aigreentick.services.auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.auth.constants.AuthConstants;
import com.aigreentick.services.auth.dto.permission.PermissionRequestDto;
import com.aigreentick.services.auth.dto.permission.PermissionResponseDto;
import com.aigreentick.services.auth.dto.permission.UserPermissionRequestDto;
import com.aigreentick.services.auth.dto.permission.UserPermissionResponseDto;
import com.aigreentick.services.auth.dto.permission.UserUserpermissionDto;
import com.aigreentick.services.auth.dto.role.RoleDto;
import com.aigreentick.services.auth.dto.role.RoleRequestDto;
import com.aigreentick.services.auth.dto.role.RoleRequestForUserDto;
import com.aigreentick.services.auth.dto.role.RoleResponseDto;
import com.aigreentick.services.auth.dto.user.UserResponseDto;
import com.aigreentick.services.auth.service.interfaces.PermissionService;
import com.aigreentick.services.auth.service.interfaces.RoleService;
import com.aigreentick.services.auth.service.interfaces.UserPermissionService;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.dto.ResponseStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AuthAdminController {
    private final RoleService roleService;
    private final UserPermissionService userPermissionService;
    private final PermissionService permissionService;

    @PostMapping("/role/add-roles")
    public ResponseEntity<?> addRole(@RequestBody RoleRequestDto roleRequestDto) {
        roleService.addRole(roleRequestDto.getName(), roleRequestDto.getDescription());
        return ResponseEntity
                .ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(),AuthConstants.ROLE_ADDED_TO_DB, null));
    }

    @PostMapping("/role/remove-role")
    public ResponseEntity<?> removeRole(@RequestBody RoleRequestDto roleRequestDto) {
        roleService.removeRole(roleRequestDto.getName());
        return ResponseEntity
                .ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), AuthConstants.ROLE_REMOVED_FROM_DB, null));
    }

    @PostMapping("/role/get-roles")
    public ResponseEntity<?> getRole(@RequestBody RoleRequestDto roleRequestDto) {
        List<RoleResponseDto> roles = roleService.getAllRoles();
        return ResponseEntity
                .ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), AuthConstants.ROLES_FETCHED_SUCCESS, roles));
    }

    @PostMapping("/role/add-role-to-user")
    public ResponseEntity<?> AddRoleToUser(@RequestBody RoleRequestForUserDto roleRequestDto) {
        UserResponseDto userResponseDto = roleService.addRoleToUser(roleRequestDto.getUserId(),
                roleRequestDto.getRoleName());
        return ResponseEntity
                .ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), AuthConstants.ROLE_ASSIGNED_TO_USER, userResponseDto));
    }

    @PostMapping("/role/remove-role-from-user")
    public ResponseEntity<?> removeRoleFromUser(@RequestBody RoleRequestForUserDto roleRequestDto) {
        UserResponseDto userResponseDto = roleService.removeRoleFromUser(roleRequestDto.getUserId(),
                roleRequestDto.getRoleName());
        return ResponseEntity
                .ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), AuthConstants.ROLE_REMOVED_FROM_USER, userResponseDto));
    }

    @PostMapping("/role/get-roles-from-user")
    public ResponseEntity<?> getUserRoles(@RequestBody RoleRequestForUserDto roleRequestDto) {
        List<RoleDto> roles = roleService.getAllRolesForUser(roleRequestDto.getUserId());
        return ResponseEntity
                .ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), AuthConstants.USER_ROLES_FETCHED, roles));
    }

    @PostMapping("/permission/add-permission")
    public ResponseEntity<?> addPermission(@RequestBody PermissionRequestDto permissionRequestDto) {
        PermissionResponseDto response = permissionService.addPermission(permissionRequestDto.getName(),
                permissionRequestDto.getDescription());
        return ResponseEntity
                .ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), AuthConstants.PERMISSION_ADDED_TO_DB, response));
    }

    @PostMapping("/permission/remove-permission")
    public ResponseEntity<?> removePermission(@RequestBody PermissionRequestDto permissionRequestDto) {
        PermissionResponseDto response = permissionService.removePermission(permissionRequestDto.getName());
        return ResponseEntity
                .ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), AuthConstants.PERMISSION_REMOVED_FROM_DB, response));
    }

    @PostMapping("/permission/get-permissions")
    public ResponseEntity<?> getPermissions() {
        List<PermissionResponseDto> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(),
                AuthConstants.PERMISSIONS_FETCHED_SUCCESS, permissions));
    }

    @PostMapping("/permission/add-permission-to-user")
    public ResponseEntity<?> setPermissionToUSer(@RequestBody UserPermissionRequestDto userPermissionRequestDto) {
        userPermissionService.setUserPermission(userPermissionRequestDto.getUserId(),
                userPermissionRequestDto.getPermissionId());
        return ResponseEntity.ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), AuthConstants.PERMISSION_ASSIGNED_TO_USER, null));
    }

    @PostMapping("/permission/remove-permission-from-user")
    public ResponseEntity<?> removePermissionFromUser(@RequestBody UserPermissionRequestDto userPermissionRequestDto) {
        UserPermissionResponseDto response = userPermissionService
                .removeUserPermission(userPermissionRequestDto.getUserId(), userPermissionRequestDto.getPermissionId());
        return ResponseEntity.ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(),
                "Permission '" + response.getPermissionName() + " 'revoked from user successfully", response));
    }

    @PostMapping("/permission/get-permissions-from-user")
    public ResponseEntity<?> getUserPermissions(@RequestBody UserUserpermissionDto request) {
        List<PermissionResponseDto> userPermissions = userPermissionService.getUserPermissions(request.getUserId());
        return ResponseEntity
                .ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), AuthConstants.USER_PERMISSIONS_FETCHED, userPermissions));
    }

}
