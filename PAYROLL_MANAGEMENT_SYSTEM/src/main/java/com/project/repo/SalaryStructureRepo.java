package com.project.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.entity.SalaryStructure;

@Repository
public interface SalaryStructureRepo extends JpaRepository<SalaryStructure, Long> {

    //  find all salary structures for a specific employee
    List<SalaryStructure> findByEmployee_Id(Long employeeId);

    //  find all salary structures for a specific organization
    List<SalaryStructure> findByEmployee_Organization_Id(Long organizationId);
}

