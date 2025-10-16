package com.project.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.dto.LoginRequestDTO;
import com.project.dto.LoginResponseDTO;
import com.project.dto.RegisterRequestDTO;
import com.project.dto.RegisterResponseDTO;
import com.project.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * ✅ Login for all users (Bank Admin, Org Admin, Employee)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid username or password", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Login failed", ex.getMessage()));
        }
    }

    @PostMapping("/register-bank-admin")
    public ResponseEntity<?> registerBankAdmin(@RequestBody RegisterRequestDTO request) {
        try {
            RegisterResponseDTO response = authService.registerBankAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Bank Admin registration failed", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Unexpected error", ex.getMessage()));
        }
    }

    @PostMapping("/register-organization")
    public ResponseEntity<?> registerOrganization(@RequestBody RegisterRequestDTO request) {
        try {
            RegisterResponseDTO response = authService.registerOrganization(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Organization registration failed", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Unexpected error", ex.getMessage()));
        }
    }

    // ✅ Inner class for clean error responses
    record ErrorResponse(String message, String details) {}
}
