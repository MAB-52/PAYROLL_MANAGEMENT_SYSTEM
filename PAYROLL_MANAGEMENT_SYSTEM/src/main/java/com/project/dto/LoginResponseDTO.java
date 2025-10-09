package com.project.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private String token;         // JWT token
    private Long userId;          // Employee, Organization, or BankAdmin ID
    private String name;          // Full name of user
    private String email;         // Email of user
    private String role;          // Role name (EMPLOYEE, ORG_ADMIN, BANK_ADMIN)
}
