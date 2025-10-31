package com.project.controllers;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.EmployeeDTO;
import com.project.security.JwtUtil;
import com.project.service.CloudinaryService;
import com.project.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final CloudinaryService cloudinaryService;
    private final JwtUtil jwtUtil; // ✅ Replaced JwtService with JwtUtil

    // ✅ Extract username from JWT token
    private String extractUsername(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String username = jwtUtil.extractUsername(token);
            if (username == null)
                throw new RuntimeException("Invalid or expired JWT token");
            return username;
        }
        throw new RuntimeException("Authorization header is missing or invalid");
    }

    @PostMapping(value = "", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<EmployeeDTO> createEmployeeWithDocument(
            @RequestPart("data") String employeeJson, // raw JSON string
            @RequestPart("document") MultipartFile document,
            @RequestHeader("Authorization") String header) throws JsonProcessingException {

        String managerUsername = extractUsername(header);

        // 1️⃣ Convert JSON string → DTO manually
        ObjectMapper mapper = new ObjectMapper();
        EmployeeDTO employeeDTO = mapper.readValue(employeeJson, EmployeeDTO.class);

        // 2️⃣ Upload to Cloudinary
        String documentUrl = cloudinaryService.uploadFile(document, "employee_docs");
        employeeDTO.setDocumentUrl(documentUrl);

        // 3️⃣ Save employee
        EmployeeDTO saved = employeeService.createEmployeeByManager(employeeDTO, managerUsername);

        return ResponseEntity.ok(saved);
    }


    // ✅ Manager fetches all employees in their organization
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getMyEmployees(
            @RequestHeader("Authorization") String header) {

        String managerUsername = extractUsername(header);
        List<EmployeeDTO> employees = employeeService.getEmployeesByManager(managerUsername);
        return ResponseEntity.ok(employees);
    }

 // ✅ Employee updates their own profile (only full name and email)
    @PutMapping("/self/update")
    public ResponseEntity<EmployeeDTO> updateOwnProfile(
            @RequestHeader("Authorization") String header,
            @RequestBody EmployeeDTO employeeDTO) {

        String username = extractUsername(header); // 🔐 Extract employee username from JWT
        EmployeeDTO updated = employeeService.updateOwnProfile(username, employeeDTO);

        return ResponseEntity.ok(updated);
    }

    // ✅ Manager deletes employee from their organization
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id,
            @RequestHeader("Authorization") String header) {

        String managerUsername = extractUsername(header);
        employeeService.deleteEmployeeByManager(id, managerUsername);
        return ResponseEntity.noContent().build();
    }

        // =========================
        // CHANGE PASSWORD
        // =========================
        @PostMapping("/change-password")
        public ResponseEntity<EmployeeDTO> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
            EmployeeDTO updatedEmployee = employeeService.changePassword(
                    request.getUsername(),
                    request.getOldPassword(),
                    request.getNewPassword()
            );
            return ResponseEntity.ok(updatedEmployee);
        }

        // DTO for password change request
        @Data
        public static class PasswordChangeRequest {
            private String username;
            private String oldPassword;
            private String newPassword;
        }
}
