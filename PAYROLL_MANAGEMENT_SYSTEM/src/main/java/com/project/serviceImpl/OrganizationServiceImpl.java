package com.project.serviceImpl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.entity.BankAdmin;
import com.project.entity.Organization;
import com.project.entity.VerificationStatus;
import com.project.repo.BankAdminRepo;
import com.project.repo.OrganizationRepo;
import com.project.service.OrganizationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepo organizationRepo;

    @Autowired
    private BankAdminRepo bankAdminRepo;

    // ✅ Verify Organization (Approve/Reject)
    @Override
    public Organization verifyOrganization(Long organizationId, Long bankAdminId, VerificationStatus status) {
        System.out.println("🟢 Verifying Organization ID: " + organizationId + ", BankAdmin ID: " + bankAdminId + ", Status: " + status);

        if (bankAdminId == null) {
            throw new IllegalArgumentException("Bank Admin ID cannot be null");
        }

        BankAdmin bankAdmin = bankAdminRepo.findById(bankAdminId)
                .orElseThrow(() -> new RuntimeException("Bank admin not found"));

        Organization org = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        org.setVerificationStatus(status);
        org.setVerifiedBy(bankAdmin); // ✅ Track who verified

        return organizationRepo.save(org);
    }

    // ✅ Create new Organization
    @Override
    public Organization createOrganization(Organization organization) {
        organization.setVerificationStatus(VerificationStatus.PENDING);
        return organizationRepo.save(organization);
    }

    // ✅ Get Organization by ID
    @Override
    public Organization getOrganizationById(Long id) {
        return organizationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found with ID: " + id));
    }

    // ✅ Get all Organizations
    @Override
    public List<Organization> getAllOrganizations() {
        return organizationRepo.findAll();
    }

    // ✅ Update organization details
    @Override
    public Organization updateOrganization(Long id, Organization updatedOrg) {
        Organization existing = getOrganizationById(id);
        existing.setOrgName(updatedOrg.getOrgName());
        existing.setRegistrationNumber(updatedOrg.getRegistrationNumber());
        existing.setContactEmail(updatedOrg.getContactEmail());
        existing.setContactPhone(updatedOrg.getContactPhone());
        existing.setAddress(updatedOrg.getAddress());
        return organizationRepo.save(existing);
    }

    // ✅ Update only verification status
    @Override
    public Organization updateVerificationStatus(Long id, VerificationStatus status) {
        Organization existing = getOrganizationById(id);
        existing.setVerificationStatus(status);
        return organizationRepo.save(existing);
    }

    // ✅ Update document (like registration proof)
    @Override
    public Organization updateDocument(Long id, String documentUrl) {
        Organization existing = getOrganizationById(id);
        existing.setDocumentUrl(documentUrl);
        return organizationRepo.save(existing);
    }

    // ✅ Delete organization
    @Override
    public void deleteOrganization(Long id) {
        organizationRepo.deleteById(id);
    }
}
