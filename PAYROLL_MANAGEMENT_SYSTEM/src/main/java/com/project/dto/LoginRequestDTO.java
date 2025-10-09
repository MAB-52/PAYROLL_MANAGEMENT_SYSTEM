package com.project.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    // User identifier: email or username
    private String username;

    // Password
    private String password;
}
