package com.project.service;

import org.springframework.web.multipart.MultipartFile;

import com.project.dto.LoginRequestDTO;
import com.project.dto.LoginResponseDTO;
import com.project.dto.RegisterRequestDTO;
import com.project.dto.RegisterResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);

    // 🔹 Separate registration methods for clarity
    RegisterResponseDTO registerBankAdmin(RegisterRequestDTO request);
    RegisterResponseDTO registerOrganization(RegisterRequestDTO request);
    
    String uploadOrganizationDocument(MultipartFile file);


}
