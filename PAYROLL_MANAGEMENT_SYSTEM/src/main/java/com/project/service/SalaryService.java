package com.project.service;

import com.project.entity.SalaryStructure;
import java.util.List;

public interface SalaryService {
    SalaryStructure createSalary(SalaryStructure salaryStructure);
    SalaryStructure getSalaryById(Long id);
    List<SalaryStructure> getAllSalaries();
    SalaryStructure updateSalary(Long id, SalaryStructure salaryStructure);
    void deleteSalary(Long id);
}
