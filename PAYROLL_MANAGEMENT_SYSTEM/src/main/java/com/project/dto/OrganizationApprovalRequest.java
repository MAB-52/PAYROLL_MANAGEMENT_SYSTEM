package com.project.dto;

import com.project.entity.VerificationStatus;
import lombok.Data;

@Data
public class OrganizationApprovalRequest {
    private VerificationStatus status; // APPROVED or REJECTED
    private Long adminId;
    private String remarks; // optional
    private String username; // for org login (if approved)
    private String password; // for org login (if approved)
}
