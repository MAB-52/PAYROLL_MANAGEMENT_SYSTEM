package com.project.service;

import com.project.dto.LoginRequestDTO;
import com.project.dto.LoginResponseDTO;
import com.project.dto.RegisterRequestDTO;
import com.project.dto.RegisterResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);

    // 🔹 Separate registration methods for clarity
    RegisterResponseDTO registerBankAdmin(RegisterRequestDTO request);
    RegisterResponseDTO registerOrganization(RegisterRequestDTO request);
}
