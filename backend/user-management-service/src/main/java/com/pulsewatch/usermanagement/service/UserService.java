package com.pulsewatch.usermanagement.service;

import com.pulsewatch.usermanagement.dto.AuthResponse;
import com.pulsewatch.usermanagement.dto.LoginRequest;
import com.pulsewatch.usermanagement.dto.RegisterRequest;
import com.pulsewatch.usermanagement.dto.UserResponse;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getUserById(Long id);
    UserResponse getUserByUsername(String username);
} 