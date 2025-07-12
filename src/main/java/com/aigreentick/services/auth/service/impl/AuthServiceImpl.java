package com.aigreentick.services.auth.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.aigreentick.services.auth.config.TokenProvider;
import com.aigreentick.services.auth.dto.LoginRequest;
import com.aigreentick.services.auth.dto.RegisterRequest;
import com.aigreentick.services.auth.dto.user.AuthResponse;
import com.aigreentick.services.auth.enums.RoleType;
import com.aigreentick.services.auth.exception.EmailAlreadyExistsException;
import com.aigreentick.services.auth.exception.MobileNumberAlreadyExistsException;
import com.aigreentick.services.auth.exception.RoleNotFoundException;
import com.aigreentick.services.auth.mapper.UserMapper;
import com.aigreentick.services.auth.model.Role;
import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.model.UserPermission;
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

    /**
     * Registers a new user with a role and automatically assigns default
     * permissions from the role.
     *
     * Steps:
     * - Validates username and mobile number uniqueness
     * - Maps DTO to entity
     * - Normalizes and fetches role
     * - Assigns role and corresponding permissions (via UserPermission mapping)
     * - Saves user with all relations
     * - Generates JWT token
     *
     * @param request the registration details
     * @return AuthResponse containing JWT and user info
     */
    @Override
    public AuthResponse register(RegisterRequest request) {
        // Step 1: Validate unique mobile and username
        checkMobileAndUsernameUniqueness(request.getMobileNumber(), request.getUsername());

        // Step 2: Convert request DTO to User entity
        User user = userMapper.toUser(request);

        // Step 3: Normalize role input (e.g., convert "admin" to "ROLE_ADMIN") and
        // fetch from DB
        RoleType normalizedRole = normalizeRole(request.getRole().name());
        Role role = roleRepository.findByName(normalizedRole)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + normalizedRole));

        // Step 4: Assign role to user
        user.setRoles(Set.of(role));

        // Step 5: Convert Role's permissions to UserPermission mappings
        Set<UserPermission> userPermissionMappings = mapRolePermissionsToUser(user, role);

        // Step 6: Set mapped permissions to user
        user.setUserPermissions(userPermissionMappings);

        // Step 7: Persist user with roles and permissions
        userRepository.save(user);

        // Step 8: Generate JWT token for immediate login
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
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

    private Set<UserPermission> mapRolePermissionsToUser(User user, Role role) {
        return role.getPermissions().stream().map(permission -> {
            UserPermission userPermission = new UserPermission();
            userPermission.setUser(user);
            userPermission.setPermission(permission);
            return userPermission;
        }).collect(Collectors.toSet());
    }

}
