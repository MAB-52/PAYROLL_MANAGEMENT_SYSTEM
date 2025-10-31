package com.project.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.LoginRequestDTO;
import com.project.dto.LoginResponseDTO;
import com.project.dto.RegisterRequestDTO;
import com.project.dto.RegisterResponseDTO;
import com.project.service.AuthService;
import com.project.service.CloudinaryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final CloudinaryService cloudinaryService;

    /**
     * ✅ Login for all users (Bank Admin, Org Admin, Employee)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
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
    public ResponseEntity<?> registerBankAdmin(@Valid @RequestBody RegisterRequestDTO request) {
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

//    @PostMapping(value = "/register-organization",  consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
//    public ResponseEntity<?> registerOrganization(
//            @RequestPart("data") @Valid RegisterRequestDTO request,
//            @RequestPart("document") MultipartFile document) {
//        try {
//            // 1️⃣ Upload to Cloudinary
//            String documentUrl = authService.uploadOrganizationDocument(document);
//
//            // 2️⃣ Set URL in DTO
//            request.setDocumentUrl(documentUrl);
//
//            // 3️⃣ Register organization
//            RegisterResponseDTO response = authService.registerOrganization(request);
//
//            return ResponseEntity.status(HttpStatus.CREATED).body(response);
//        } catch (RuntimeException ex) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(new ErrorResponse("Organization registration failed", ex.getMessage()));
//        } catch (Exception ex) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ErrorResponse("Unexpected error", ex.getMessage()));
//        }
//    }
    
    @PostMapping(value = "/register-organization", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerOrganization(
            @RequestPart("data") String data,
            @RequestPart("document") MultipartFile document) {

        try {
            // 1️⃣ Convert JSON string to DTO
            ObjectMapper mapper = new ObjectMapper();
            RegisterRequestDTO request = mapper.readValue(data, RegisterRequestDTO.class);

            // 2️⃣ Upload document to Cloudinary
            String documentUrl = authService.uploadOrganizationDocument(document);

            // 3️⃣ Set the document URL in DTO
            request.setDocumentUrl(documentUrl);

            // 4️⃣ Register organization
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
