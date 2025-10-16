package com.project.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponseDTO {
    private String message;
    private String username;
    private String role;
    private String email;
    //private Long organizationId;
}
