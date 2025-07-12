package com.aigreentick.services.auth.service.interfaces;

import com.aigreentick.services.auth.dto.LoginRequest;
import com.aigreentick.services.auth.dto.RegisterRequest;
import com.aigreentick.services.auth.dto.user.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest input);
    AuthResponse login(LoginRequest input);
}