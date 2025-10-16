package com.project.service;

import java.util.List;
import com.project.dto.OrganizationDTO;
import com.project.entity.Organization;
import com.project.entity.VerificationStatus;

public interface OrganizationService {

    Organization createOrganization(Organization organization);

    Organization getOrganizationById(Long id);

    List<Organization> getAllOrganizations();

    Organization updateOrganization(Long id, OrganizationDTO organizationDTO);

    void deleteOrganization(Long id);

    Organization verifyOrganization(Long organizationId, Long bankAdminId, VerificationStatus status);

    Organization updateVerificationStatus(Long id, VerificationStatus status);

    Organization updateDocument(Long id, String documentUrl);
}
