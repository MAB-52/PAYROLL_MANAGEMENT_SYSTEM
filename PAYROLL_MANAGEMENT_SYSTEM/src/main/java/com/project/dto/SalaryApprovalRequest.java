package com.project.dto;

import lombok.Data;
import com.project.entity.SalaryStatus;

@Data
public class SalaryApprovalRequest {
    private Long adminId;
    private SalaryStatus status; // COMPLETED or FAILED
    private String remarks; // optional
}
