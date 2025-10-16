package com.project.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.entity.ClientVendor;
import com.project.entity.Organization;
import com.project.repo.ClientVendorRepo;
import com.project.repo.OrganizationRepo;
import com.project.service.ClientVendorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientVendorServiceImpl implements ClientVendorService {

    private final ClientVendorRepo clientVendorRepo;
    private final OrganizationRepo organizationRepo;

    @Override
    public ClientVendor createVendor(Long orgId, ClientVendor vendor) {
        Organization org = organizationRepo.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found with ID: " + orgId));

        vendor.setOrganization(org);
        return clientVendorRepo.save(vendor);
    }

    @Override
    public List<ClientVendor> getVendorsByOrganization(Long orgId) {
        Organization org = organizationRepo.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found with ID: " + orgId));

        return clientVendorRepo.findByOrganization(org);
    }
}
