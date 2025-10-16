package com.project.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.VendorPayment;

public interface VendorPaymentRepo extends JpaRepository<VendorPayment, Long> {
    List<VendorPayment> findByOrganizationId(Long organizationId);
}
