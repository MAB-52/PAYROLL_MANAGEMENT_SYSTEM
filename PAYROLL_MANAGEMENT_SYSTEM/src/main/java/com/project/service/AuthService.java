package com.project.service;

import com.project.dto.LoginRequestDTO;
import com.project.dto.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO request);
}
