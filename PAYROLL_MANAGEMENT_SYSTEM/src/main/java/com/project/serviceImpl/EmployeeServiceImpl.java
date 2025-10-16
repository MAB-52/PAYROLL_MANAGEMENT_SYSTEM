package com.project.serviceImpl;

import com.project.dto.EmployeeDTO;
import com.project.entity.Employee;
import com.project.entity.Organization;
import com.project.entity.User;
import com.project.entity.VerificationStatus;
import com.project.mapper.EntityMapper;
import com.project.repo.EmployeeRepo;
import com.project.repo.OrganizationRepo;
import com.project.repo.UserRepo;
import com.project.security.JwtUtil;
import com.project.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepo employeeRepository;
    private final OrganizationRepo organizationRepository;
    private final EntityMapper entityMapper;
    private final UserRepo userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder; // ✅ Added

    // =========================
    // CREATE EMPLOYEE (BANK ADMIN)
    // =========================
    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Organization organization = organizationRepository.findById(employeeDTO.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found with ID: " + employeeDTO.getOrganizationId()));

        Employee employee = entityMapper.toEmployeeEntity(employeeDTO, organization);

        // ✅ Encrypt password before saving
        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }

        Employee saved = employeeRepository.save(employee);
        return entityMapper.toEmployeeDTO(saved);
    }

    // =========================
    // GET EMPLOYEE BY ID
    // =========================
    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));
        return entityMapper.toEmployeeDTO(employee);
    }

    // =========================
    // GET ALL EMPLOYEES
    // =========================
    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(entityMapper::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    // =========================
    // GET EMPLOYEES BY ORGANIZATION
    // =========================
    @Override
    public List<EmployeeDTO> getEmployeesByOrganization(Long organizationId) {
        List<Employee> employees = employeeRepository.findByOrganizationId(organizationId);
        return employees.stream()
                .map(entityMapper::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    // =========================
    // UPDATE EMPLOYEE (BANK ADMIN)
    // =========================
    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));

        existing.setFullName(employeeDTO.getFullName());
        existing.setEmail(employeeDTO.getEmail());
        existing.setDepartment(employeeDTO.getDepartment());
        existing.setDesignation(employeeDTO.getDesignation());
        existing.setAccountNumber(employeeDTO.getAccountNumber());
        existing.setIfscCode(employeeDTO.getIfscCode());
        existing.setBankName(employeeDTO.getBankName());
        existing.setDocumentUrl(employeeDTO.getDocumentUrl());
        existing.setVerificationStatus(employeeDTO.getVerificationStatus() != null
                ? Enum.valueOf(VerificationStatus.class, employeeDTO.getVerificationStatus())
                : existing.getVerificationStatus());
        existing.setSalaryStructure(entityMapper.toSalaryEntity(employeeDTO.getSalaryStructure()));

        Employee updated = employeeRepository.save(existing);
        return entityMapper.toEmployeeDTO(updated);
    }

    // =========================
    // DELETE EMPLOYEE
    // =========================
    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    // =========================
    // MANAGER-SPECIFIC: CREATE EMPLOYEE
    // =========================
    @Override
    public EmployeeDTO createEmployeeByManager(EmployeeDTO employeeDTO, String managerUsername) {
        User manager = userRepository.findByUsername(managerUsername)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        Organization org = manager.getOrganization();
        if (org == null)
            throw new RuntimeException("Manager is not linked to any organization");

        Employee employee = entityMapper.toEmployeeEntity(employeeDTO, org);
        employee.setVerificationStatus(VerificationStatus.PENDING);

        // ✅ Encode password
        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }

        Employee saved = employeeRepository.save(employee);
        return entityMapper.toEmployeeDTO(saved);
    }

    // =========================
    // MANAGER: GET EMPLOYEES
    // =========================
    @Override
    public List<EmployeeDTO> getEmployeesByManager(String managerUsername) {
        User manager = userRepository.findByUsername(managerUsername)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        Organization org = manager.getOrganization();

        List<Employee> employees = employeeRepository.findByOrganizationId(org.getId());
        return employees.stream()
                .map(entityMapper::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    // =========================
    // MANAGER: UPDATE EMPLOYEE
    // =========================
    @Override
    public EmployeeDTO updateEmployeeByManager(Long id, EmployeeDTO dto, String managerUsername) {
        User manager = userRepository.findByUsername(managerUsername)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        Organization org = manager.getOrganization();

        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!existing.getOrganization().getId().equals(org.getId()))
            throw new RuntimeException("You can only update employees in your organization");

        existing.setFullName(dto.getFullName());
        existing.setDepartment(dto.getDepartment());
        existing.setDesignation(dto.getDesignation());
        existing.setBankName(dto.getBankName());
        existing.setIfscCode(dto.getIfscCode());
        existing.setAccountNumber(dto.getAccountNumber());
        existing.setDocumentUrl(dto.getDocumentUrl());

        Employee updated = employeeRepository.save(existing);
        return entityMapper.toEmployeeDTO(updated);
    }

    // =========================
    // MANAGER: DELETE EMPLOYEE
    // =========================
    @Override
    public void deleteEmployeeByManager(Long id, String managerUsername) {
        User manager = userRepository.findByUsername(managerUsername)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        Organization org = manager.getOrganization();

        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!emp.getOrganization().getId().equals(org.getId()))
            throw new RuntimeException("You can only delete employees in your organization");

        employeeRepository.delete(emp);
    }

    // =========================
    // APPROVE / REJECT EMPLOYEE STATUS
    // =========================
    @Override
    public EmployeeDTO approveEmployeeStatus(Long employeeId, String status, String remarks) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        VerificationStatus verStatus;
        try {
            verStatus = VerificationStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Invalid status. Must be APPROVED or REJECTED");
        }

        employee.setVerificationStatus(verStatus);
        employeeRepository.save(employee);
        return entityMapper.toEmployeeDTO(employee);
    }

    // =========================
    // EMPLOYEE LOGIN (JWT)
    // =========================
    @Override
    public String loginEmployee(String username, String password) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // ✅ Compare hashed password
        if (!passwordEncoder.matches(password, employee.getPassword()))
            throw new RuntimeException("Invalid username or password");

        if (employee.getVerificationStatus() != VerificationStatus.APPROVED)
            throw new RuntimeException("Employee not approved by bank admin");

        // ✅ Include roles for JWT
        List<String> roles = List.of("EMPLOYEE");

        // ✅ Updated to match JwtUtil signature
        return jwtUtil.generateToken(username, roles);
    }

}
