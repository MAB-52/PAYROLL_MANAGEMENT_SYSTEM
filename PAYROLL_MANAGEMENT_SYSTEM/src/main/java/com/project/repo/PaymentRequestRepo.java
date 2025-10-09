package com.project.repo;

import com.project.entity.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRequestRepo extends JpaRepository<PaymentRequest, Long> {
    List<PaymentRequest> findByOrganizationId(Long organizationId);
}
