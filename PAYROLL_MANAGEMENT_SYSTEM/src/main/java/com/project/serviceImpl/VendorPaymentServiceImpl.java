package com.project.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.project.entity.VendorPayment;
import com.project.entity.ClientVendor;
import com.project.entity.Organization;
import com.project.entity.SalaryStatus;
import com.project.repo.VendorPaymentRepo;
import com.project.repo.OrganizationRepo;
import com.project.repo.ClientVendorRepo;
import com.project.service.VendorPaymentService;

@Service
@RequiredArgsConstructor
public class VendorPaymentServiceImpl implements VendorPaymentService {

    private final VendorPaymentRepo vendorPaymentRepo;
    private final OrganizationRepo organizationRepo;
    private final ClientVendorRepo clientVendorRepo;

    public VendorPayment requestPayment(Long orgId, VendorPayment paymentRequest) {
        Organization org = organizationRepo.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Optional: fetch the vendor
        ClientVendor vendor = clientVendorRepo.findById(paymentRequest.getVendor().getCvId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        VendorPayment payment = VendorPayment.builder()
                .organization(org)
                .vendor(vendor)
                .amount(paymentRequest.getAmount())
                .remarks(paymentRequest.getRemarks())
                .status(SalaryStatus.PENDING)
                .requestDate(LocalDateTime.now()) // ✅ Set the request date here
                .build();

        return vendorPaymentRepo.save(payment);
    }

}
