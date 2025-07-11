package com.aigreentick.services.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.auth.dto.RoleRequestDto;
import com.aigreentick.services.auth.dto.RoleRequestForUserDto;
import com.aigreentick.services.auth.dto.UserResponseDto;
import com.aigreentick.services.auth.service.interfaces.RoleService;
import com.aigreentick.services.common.dto.ResponseMessage;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AuthAdminController {
    private final RoleService roleService;
    
    @PostMapping("/add-roles")
    public ResponseEntity<?> addRole(@RequestBody RoleRequestDto roleRequestDto ){
        roleService.addRole(roleRequestDto.getName(),roleRequestDto.getDescription());
        return ResponseEntity.ok(new ResponseMessage<>("success","Role added into database",null));
    }

    @PostMapping("/remove-role")
    public ResponseEntity<?> removeRole(@RequestBody RoleRequestDto roleRequestDto ){
        roleService.removeRole(roleRequestDto.getName());
        return ResponseEntity.ok(new ResponseMessage<>("success","Role removed from database",null));
    }

    @PostMapping("/add-role-to-user")
    public ResponseEntity<?> AddRoleToUser(@RequestBody RoleRequestForUserDto roleRequestDto ){
        UserResponseDto userResponseDto = roleService.addRoleToUser(roleRequestDto.getUserId(),roleRequestDto.getRoleName());
        return ResponseEntity.ok(new ResponseMessage<>("success","Role added to user",userResponseDto));
    }

    @PostMapping("/remove-role-from-user")
    public ResponseEntity<?> removeRoleFromUser(@RequestBody RoleRequestForUserDto roleRequestDto ){
        UserResponseDto userResponseDto = roleService.removeRoleFromUser(roleRequestDto.getUserId(),roleRequestDto.getRoleName());
        return ResponseEntity.ok(new ResponseMessage<>("success","Role removed from user",userResponseDto));
    }

}
