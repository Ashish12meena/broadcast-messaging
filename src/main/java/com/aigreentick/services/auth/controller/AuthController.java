package com.aigreentick.services.auth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.auth.constants.AuthConstants;
import com.aigreentick.services.auth.dto.AuthResponse;
import com.aigreentick.services.auth.dto.LoginRequest;
import com.aigreentick.services.auth.dto.RegisterRequest;
import com.aigreentick.services.auth.dto.RoleResponseDto;
import com.aigreentick.services.auth.service.interfaces.AuthService;
import com.aigreentick.services.auth.service.interfaces.RoleService;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.dto.ResponseStatus;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RoleService roleService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest input) {
        AuthResponse authResponse = authService.register(input);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage<>(ResponseStatus.SUCCESS.name(),AuthConstants.USER_CREATED , authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest input) {
        AuthResponse authResponse = authService.login(input);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), AuthConstants.USER_LOGIN, authResponse));
    }

    @PostMapping("/get-roles")
    public List<RoleResponseDto> getAllRoles(){
        return roleService.getAllRoles();
    }



}
