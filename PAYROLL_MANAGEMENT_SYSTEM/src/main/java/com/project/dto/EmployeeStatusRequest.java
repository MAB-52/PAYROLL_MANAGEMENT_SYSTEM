package com.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeStatusRequest {
    private String status;  // "APPROVED" or "REJECTED"
    private String remarks; // optional
}
