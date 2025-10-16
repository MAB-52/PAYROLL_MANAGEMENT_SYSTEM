package com.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {

    // 🔹 Common Fields
    private String name;
    private String email;
    private String password;
    private String contactNumber;

    // 🔹 For BANK_ADMIN
    private Long bankId; // Reference to Bank entity

    // 🔹 For ORGANIZATION
    private String orgName;
    private String registrationNumber;
    private String contactPhone;
    private String documentUrl;
    private String address;

    // 🔹 Nested User Info (for linked User entity)
    private UserDTO user;
}
