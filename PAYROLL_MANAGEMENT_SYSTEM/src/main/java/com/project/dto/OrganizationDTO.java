package com.project.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationDTO {

    private Long id;
    private String organizationName;
    private String email;
    private String contactNumber;
    private String address;
    private String registrationNumber;
    private String documentUrl; // Cloudinary doc
    private String verificationStatus; // PENDING / APPROVED / REJECTED
    private Long bankId; // to link with Bank
    private List<Long> employeeIds; // optional summary
    private List<Long> paymentRequestIds; // payment requests
    

}
