package com.project.controllers;

import com.project.dto.EmployeeDTO;
import com.project.security.JwtUtil;
import com.project.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
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

    // ✅ Manager creates an employee
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(
            @RequestBody EmployeeDTO employeeDTO,
            @RequestHeader("Authorization") String header) {

        String managerUsername = extractUsername(header);
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

    // ✅ Manager updates employee in their organization
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeDTO employeeDTO,
            @RequestHeader("Authorization") String header) {

        String managerUsername = extractUsername(header);
        EmployeeDTO updated = employeeService.updateEmployeeByManager(id, employeeDTO, managerUsername);
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
}
