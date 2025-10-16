package com.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.entity.ClientVendor;
import com.project.entity.Organization;

import java.util.List;

@Repository
public interface ClientVendorRepo extends JpaRepository<ClientVendor, Long> {

    // Find all vendors for a specific organization
    List<ClientVendor> findByOrganization(Organization organization);

    // Optional: find by type (CLIENT or VENDOR)
    List<ClientVendor> findByOrganizationAndType(Organization organization, Enum type);

    // Optional: find by email
    ClientVendor findByEmail(String email);
}
