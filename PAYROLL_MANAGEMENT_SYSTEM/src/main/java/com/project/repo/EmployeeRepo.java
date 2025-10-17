package com.project.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.entity.Employee;
import com.project.entity.VerificationStatus;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);

    // optional: if employees will have username field in future
    Optional<Employee> findByUsername(String username);
    
    List<Employee> findByOrganizationId(Long organizationId);
    List<Employee> findByVerificationStatus(VerificationStatus status);
}
