package com.project.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String password;
    private List<String> roles; // e.g., EMPLOYEE, ORG_ADMIN, BANK_ADMIN
}

