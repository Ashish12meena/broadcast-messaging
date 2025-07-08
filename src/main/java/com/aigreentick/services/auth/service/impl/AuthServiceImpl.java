package com.aigreentick.services.auth.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.aigreentick.services.auth.config.TokenProvider;
import com.aigreentick.services.auth.dto.AuthResponse;
import com.aigreentick.services.auth.dto.LoginRequest;
import com.aigreentick.services.auth.dto.RegisterRequest;
import com.aigreentick.services.auth.enums.RoleType;
import com.aigreentick.services.auth.exception.EmailAlreadyExistsException;
import com.aigreentick.services.auth.exception.MobileNumberAlreadyExistsException;
import com.aigreentick.services.auth.exception.RoleNotFoundException;
import com.aigreentick.services.auth.mapper.UserMapper;
import com.aigreentick.services.auth.model.Role;
import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.repository.RoleRepository;
import com.aigreentick.services.auth.repository.UserRepository;
import com.aigreentick.services.auth.service.interfaces.AuthService;
import com.aigreentick.services.common.exception.ResourceAlreadyExistsException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final TokenProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        checkMobileAndUsernameUniqueness(request.getMobileNumber(), request.getUsername());
        User user = userMapper.toUser(request);
        RoleType roleName = normalizeRole(request.getRole().name());
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));
        user.setRole(role);
        userRepository.save(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String authToken = jwtProvider.generateToken(userDetails);
        return new AuthResponse(authToken, userMapper.toUserResponseDto(user));
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            String authToken = generateToken(loginRequest.getUsername(), loginRequest.getPassword());
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return new AuthResponse(authToken, userMapper.toUserResponseDto(user));
        } catch (Exception e) {
            throw new UsernameNotFoundException("User Not Found");
        }

    }

    private void checkMobileAndUsernameUniqueness(String mobile, String username) {
        Object[] result = (Object[]) userRepository.checkMobileAndUsernameExist(mobile, username);

        Long mobileCount = result[0] == null ? 0L : ((Number) result[0]).longValue();
        Long usernameCount = result[1] == null ? 0L : ((Number) result[1]).longValue();

        if (mobileCount > 0 && usernameCount > 0) {
            throw new ResourceAlreadyExistsException("Mobile number and username already exist");
        } else if (mobileCount > 0) {
            throw new MobileNumberAlreadyExistsException("mobile Number already exist");
        } else if (usernameCount > 0) {
            throw new EmailAlreadyExistsException("Email already exist");

        }

    }

    private RoleType normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return RoleType.ROLE_USER;
        }
        try {
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role.toUpperCase();
            } else {
                role = role.toUpperCase();
            }
            return RoleType.valueOf(role);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    private String generateToken(String username, String rawPassword) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, rawPassword));
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtProvider.generateToken(userDetails);
    }

}
