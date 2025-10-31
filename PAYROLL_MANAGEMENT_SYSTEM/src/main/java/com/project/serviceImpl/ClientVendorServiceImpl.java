package com.project.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.entity.ClientVendor;
import com.project.entity.Organization;
import com.project.repo.ClientVendorRepo;
import com.project.repo.OrganizationRepo;
import com.project.service.ClientVendorService;
import com.project.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientVendorServiceImpl implements ClientVendorService {

    private final ClientVendorRepo clientVendorRepo;
    private final OrganizationRepo organizationRepo;
    private final EmailService emailService;

    @Override
    public ClientVendor createVendor(Long orgId, ClientVendor vendor) {
        Organization org = organizationRepo.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found with ID: " + orgId));

        vendor.setOrganization(org);
        ClientVendor savedVendor = clientVendorRepo.save(vendor);

        // ✅ Send registration email to vendor
        if (savedVendor.getEmail() != null && !savedVendor.getEmail().isBlank()) {
            String emailBody = String.format(
                    "Dear %s,\n\n" +
                    "You have been successfully registered as a vendor with organization '%s'.\n\n" +
                    "Vendor Details:\n" +
                    "Vendor Name: %s\n" +
                    "Vendor ID: %d\n" +
                    "Organization: %s\n" +
                    "Email: %s\n" +
                    "Contact Number: %s\n" +
                    "Status: Registered Successfully (Pending Payment)\n\n" +
                    "Regards,\nPayroll Management System",
                    savedVendor.getName(),
                    org.getOrgName(),
                    savedVendor.getName(),
                    savedVendor.getCvId(),
                    org.getOrgName(),
                    savedVendor.getEmail(),
                    savedVendor.getGstNumber()
            );

            emailService.sendEmail(savedVendor.getEmail(), "Vendor Registration Successful", emailBody);
        }

        return savedVendor;
    }


    @Override
    public List<ClientVendor> getVendorsByOrganization(Long orgId) {
        Organization org = organizationRepo.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found with ID: " + orgId));

        return clientVendorRepo.findByOrganization(org);
    }
}
