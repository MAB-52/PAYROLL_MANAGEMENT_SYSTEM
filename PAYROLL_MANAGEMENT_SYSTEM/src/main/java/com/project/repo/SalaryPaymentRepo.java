package com.project.repo;

import com.project.entity.SalaryPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryPaymentRepo extends JpaRepository<SalaryPayment, Long> {
    List<SalaryPayment> findByEmployeeId(Long employeeId);
    List<SalaryPayment> findByOrganizationId(Long organizationId);
    List<SalaryPayment> findByOrganizationIdAndSalaryMonth(Long organizationId, String salaryMonth);
}
