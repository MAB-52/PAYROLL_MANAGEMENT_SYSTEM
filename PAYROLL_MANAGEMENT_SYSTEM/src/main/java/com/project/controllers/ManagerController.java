package com.project.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.dto.SalaryPaymentDTO;
import com.project.dto.VendorPaymentDTO;
import com.project.entity.SalaryPayment;
import com.project.entity.VendorPayment;
import com.project.service.SalaryPaymentService;
import com.project.service.VendorPaymentService;
import com.project.serviceImpl.ManagerValidationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {

    private final SalaryPaymentService salaryPaymentService;
    private final ManagerValidationService managerValidationService;
    private final VendorPaymentService vendorPaymentService;

    @PostMapping("/organizations/{orgId}/salary/request")
    public ResponseEntity<List<SalaryPaymentDTO>> createSalaryRequest(
            @PathVariable Long orgId,
            @RequestBody SalaryPayment request,
            Principal principal
    ) {
        String username = principal.getName(); // From JWT

        // Validate Manager & Organization
        managerValidationService.validateManagerInOrganization(username, orgId);

        List<SalaryPaymentDTO> payments = salaryPaymentService.createSalaryDisbursal(
                orgId,
                request.getSalaryMonth()
        );
        return ResponseEntity.ok(payments);
    }
    
    @PostMapping("/organizations/{orgId}/vendor-payment/request")
    public ResponseEntity<VendorPaymentDTO> createVendorPaymentRequest(
            @PathVariable Long orgId,
            @RequestBody VendorPayment paymentRequest,
            Principal principal
    ) {
        String username = principal.getName(); // From JWT

        // Validate Manager & Organization
        managerValidationService.validateManagerInOrganization(username, orgId);

        // Create vendor payment
        VendorPayment payment = vendorPaymentService.requestPayment(orgId, paymentRequest);

        VendorPaymentDTO dto = VendorPaymentDTO.builder()
                .id(payment.getId())
                .vendorId(payment.getVendor().getCvId())
                .vendorName(payment.getVendor().getName())
                .organizationId(payment.getOrganization().getId())
                .amount(payment.getAmount())
                .remarks(payment.getRemarks())
                .status(payment.getStatus().name())
                .verifiedByAdminId(payment.getVerifiedBy() != null ? payment.getVerifiedBy().getId() : null)
                .requestDate(payment.getRequestDate()) // ✅ correct
                .build();

        return ResponseEntity.ok(dto);
    }
}
