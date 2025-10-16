package com.project.serviceImpl;

import com.project.entity.*;
import com.project.dto.OrganizationDTO;
import com.project.entity.VerificationStatus;
import com.project.exception.ResourceNotFoundException;
import com.project.mapper.EntityMapper;
import com.project.repo.BankRepo;
import com.project.repo.OrganizationRepo;
import com.project.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private BankRepo bankRepo;

    @Autowired
    private EntityMapper entityMapper;

    // =============================
    // CREATE ORGANIZATION
    // =============================
    @Override
    public Organization createOrganization(Organization organization) {
        organization.setVerificationStatus(VerificationStatus.PENDING);

        // ✅ Validate and set bank reference properly
        if (organization.getBank() == null || organization.getBank().getId() == null) {
            throw new RuntimeException("Bank reference is required");
        }

        Bank bank = bankRepo.findById(organization.getBank().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bank not found with ID: " + organization.getBank().getId()));

        organization.setBank(bank);

        // ✅ Validation for email
        if (organization.getContactEmail() == null || organization.getContactEmail().trim().isEmpty()) {
            throw new RuntimeException("Contact email is required");
        }

        return organizationRepo.save(organization);
    }

    // =============================
    // GET ALL ORGANIZATIONS
    // =============================
    @Override
    public List<Organization> getAllOrganizations() {
        return organizationRepo.findAll();
    }

    // =============================
    // GET ORGANIZATION BY ID
    // =============================
    @Override
    public Organization getOrganizationById(Long id) {
        return organizationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with ID: " + id));
    }

    // =============================
    // UPDATE ORGANIZATION
    // =============================
    @Override
    public Organization updateOrganization(Long id, OrganizationDTO dto) {
        Organization existing = organizationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with ID: " + id));

        // ✅ Fetch bank if provided
        Bank bank = null;
        if (dto.getBankId() != null) {
            bank = bankRepo.findById(dto.getBankId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bank not found with ID: " + dto.getBankId()));
        }

        // ✅ Map updates
        existing.setOrgName(dto.getOrgName());
        existing.setRegistrationNumber(dto.getRegistrationNumber());
        existing.setContactEmail(dto.getEmail());
        existing.setContactPhone(dto.getContactNumber());
        existing.setAddress(dto.getAddress());
        existing.setDocumentUrl(dto.getDocumentUrl());
        existing.setBank(bank);
        existing.setVerificationStatus(dto.getVerificationStatus() != null ?
                VerificationStatus.valueOf(dto.getVerificationStatus()) : existing.getVerificationStatus());

        return organizationRepo.save(existing);
    }

    // =============================
    // DELETE ORGANIZATION
    // =============================
    @Override
    public void deleteOrganization(Long id) {
        Organization org = organizationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with ID: " + id));
        organizationRepo.delete(org);
    }
    
    @Override
    public Organization verifyOrganization(Long organizationId, Long bankAdminId, VerificationStatus status) {
        Organization org = getOrganizationById(organizationId);
        org.setVerificationStatus(status);
        return organizationRepo.save(org);
    }

    @Override
    public Organization updateVerificationStatus(Long id, VerificationStatus status) {
        Organization org = getOrganizationById(id);
        org.setVerificationStatus(status);
        return organizationRepo.save(org);
    }

    @Override
    public Organization updateDocument(Long id, String documentUrl) {
        Organization org = getOrganizationById(id);
        org.setDocumentUrl(documentUrl);
        return organizationRepo.save(org);
    }

}
