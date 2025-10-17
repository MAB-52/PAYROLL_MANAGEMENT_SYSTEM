package com.project.service;

import com.project.dto.EmployeeDTO;
import java.util.List;

public interface EmployeeService {
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);
    EmployeeDTO getEmployeeById(Long id);
    List<EmployeeDTO> getAllEmployees();
    List<EmployeeDTO> getEmployeesByOrganization(Long organizationId);
    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO);
    void deleteEmployee(Long id);
    
    EmployeeDTO createEmployeeByManager(EmployeeDTO employeeDTO, String managerUsername);
    List<EmployeeDTO> getEmployeesByManager(String managerUsername);
    EmployeeDTO updateEmployeeByManager(Long id, EmployeeDTO employeeDTO, String managerUsername);
    void deleteEmployeeByManager(Long id, String managerUsername);
    EmployeeDTO approveEmployeeStatus(Long employeeId, String status, String remarks);
    String loginEmployee(String username, String password);
    List<EmployeeDTO> getPendingVerifications();

}
