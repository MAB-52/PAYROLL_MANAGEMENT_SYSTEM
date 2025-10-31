package com.project.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.dto.OrganizationDTO;
import com.project.entity.Bank;
import com.project.entity.Organization;
import com.project.entity.VerificationStatus;
import com.project.exception.ResourceNotFoundException;
import com.project.mapper.EntityMapper;
import com.project.repo.BankRepo;
import com.project.service.CloudinaryService;
import com.project.service.OrganizationService;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organizations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;
    private final EntityMapper entityMapper;
    private final BankRepo bankRepo; // ✅ Inject BankRepo properly

    private final CloudinaryService cloudinaryService;

    // ✅ Register organization with document upload
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<OrganizationDTO> createOrganization(
            @RequestPart("data") @Valid OrganizationDTO dto,
            @RequestPart(value = "document", required = false) MultipartFile document) {

        Bank bank = null;
        if (dto.getBankId() != null) {
            bank = bankRepo.findById(dto.getBankId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bank not found with ID: " + dto.getBankId()));
        }

        Organization org = entityMapper.toOrganizationEntity(dto, bank);

        // ✅ Upload document if provided
        if (document != null && !document.isEmpty()) {
            String documentUrl = cloudinaryService.uploadFile(document, "organization_documents");
            org.setDocumentUrl(documentUrl);
        }

        Organization saved = organizationService.createOrganization(org);
        return ResponseEntity.ok(entityMapper.toOrganizationDTO(saved));
    }

    // ✅ Get all organizations
    @GetMapping
    public ResponseEntity<List<OrganizationDTO>> getAllOrganizations() {
        List<OrganizationDTO> list = organizationService.getAllOrganizations().stream()
                .map(entityMapper::toOrganizationDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    // ✅ Get single organization by ID
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDTO> getOrganizationById(@PathVariable Long id) {
        Organization org = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(entityMapper.toOrganizationDTO(org));
    }

    // ✅ Verify or Reject organization
    @PutMapping("/{organizationId}/verify")
    public ResponseEntity<OrganizationDTO> verifyOrganization(
            @PathVariable Long organizationId,
            @Valid @RequestBody VerificationRequest request) {

        Organization updated = organizationService.verifyOrganization(
                organizationId,
                request.getAdminId(),
                VerificationStatus.valueOf(request.getStatus())
        );

        return ResponseEntity.ok(entityMapper.toOrganizationDTO(updated));
    }

    // ✅ Update organization details
    @PutMapping("/{id}")
    public ResponseEntity<OrganizationDTO> updateOrganization(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationDTO dto) {

        Organization updated = organizationService.updateOrganization(id, dto);
        return ResponseEntity.ok(entityMapper.toOrganizationDTO(updated));
    }

    // ✅ Delete organization
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Inner class for verification request
    @Data
    public static class VerificationRequest {
        private String status;
        private Long adminId;
    }
}
