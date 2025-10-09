package com.project.dto;

import com.project.entity.VerificationStatus;
import lombok.Data;

@Data
public class OrganizationApprovalRequest {
    private VerificationStatus status;  // APPROVED / REJECTED
    private Long adminId;
    private String remarks;  // Optional for rejection
}
