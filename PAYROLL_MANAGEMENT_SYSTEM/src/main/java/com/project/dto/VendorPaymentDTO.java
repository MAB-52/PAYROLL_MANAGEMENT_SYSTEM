package com.project.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class VendorPaymentDTO {
    private Long id;
    private Long vendorId;
    private String vendorName;
    private Long organizationId;
    private Double amount;
    private String remarks;
    private String status;
    private Long verifiedByAdminId;
    private LocalDateTime requestDate;
}
