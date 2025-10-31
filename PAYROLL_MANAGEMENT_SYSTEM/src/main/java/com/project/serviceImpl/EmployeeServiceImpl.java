package com.project.serviceImpl;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.dto.EmployeeDTO;
import com.project.entity.BankAdmin;
import com.project.entity.Employee;
import com.project.entity.Organization;
import com.project.entity.User;
import com.project.entity.VerificationStatus;
import com.project.exception.ResourceNotFoundException;
import com.project.mapper.EntityMapper;
import com.project.repo.BankAdminRepo;
import com.project.repo.EmployeeRepo;
import com.project.repo.OrganizationRepo;
import com.project.repo.UserRepo;
import com.project.security.JwtUtil;
import com.project.service.EmailService;
import com.project.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepo employeeRepository;
    private final OrganizationRepo organizationRepository;
    private final BankAdminRepo bankAdminRepository;
    private final EntityMapper entityMapper;
    private final UserRepo userRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder; // ✅ Added

    // =========================
    // CREATE EMPLOYEE (BANK ADMIN)
    // =========================
 // 🔹 Bank Admin creates employee
    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Organization organization = organizationRepository.findById(employeeDTO.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with ID: " + employeeDTO.getOrganizationId()));

        Employee employee = entityMapper.toEmployeeEntity(employeeDTO, organization);

        // keep plain password before encoding
        String plainPassword = employee.getPassword();
        if (plainPassword != null && !plainPassword.isEmpty()) {
            employee.setPassword(passwordEncoder.encode(plainPassword));
        }

        Employee saved = employeeRepository.save(employee);

        // send plain password email
        if (plainPassword != null && !plainPassword.isEmpty()) {
            String emailBody = String.format(
                    "Dear %s,\n\n" +
                            "Your account has been created successfully.\n\n" +
                            "Username: %s\n" +
                            "Email: %s\n" +
                            "Password: %s\n\n" +
                            "Please change your password after your first login.\n\n" +
                            "Regards,\nPayroll Management System",
                    saved.getFullName(), saved.getUsername(), saved.getEmail(), plainPassword);
            emailService.sendEmail(saved.getEmail(), "Employee Account Created", emailBody);
        }

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
    
// // 🔹 Manager creates employee (Pending Verification)
//    @Override
//    public EmployeeDTO createEmployeeByManager(EmployeeDTO employeeDTO, String managerUsername) {
//        User manager = userRepository.findByUsername(managerUsername)
//                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
//
//        Organization organization = manager.getOrganization();
//        if (organization == null)
//            throw new RuntimeException("Manager is not linked to any organization");
//
//        Employee employee = entityMapper.toEmployeeEntity(employeeDTO, organization);
//        String plainPassword = employee.getPassword();
//
//        employee.setVerificationStatus(VerificationStatus.PENDING);
//if (plainPassword != null && !plainPassword.isEmpty()) {
//            employee.setPassword(passwordEncoder.encode(plainPassword));
//        }
//
//        Employee saved = employeeRepository.save(employee);
//
//        // Send pending verification email
//        if (plainPassword != null && !plainPassword.isEmpty()) {
//            String emailBody = String.format(
//                    "Dear %s,\n\n" +
//                            "Your account has been created and is pending verification.\n\n" +
//                            "Username: %s\n" +
//                            "Email: %s\n" +
//                            "Password: %s\n\n" +
//                            "You will be notified once verification is complete.\n\n" +
//                            "Regards,\nPayroll Management System",
//                    saved.getFullName(), saved.getUsername(), saved.getEmail(), plainPassword);
//            emailService.sendEmail(saved.getEmail(), "Account Pending Verification", emailBody);
//        }
//
//        return entityMapper.toEmployeeDTO(saved);
//    }
    
    @Override
    public EmployeeDTO createEmployeeByManager(EmployeeDTO employeeDTO, String managerUsername) {
        User manager = userRepository.findByUsername(managerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));

        Organization organization = manager.getOrganization();
        if (organization == null)
            throw new RuntimeException("Manager is not linked to any organization");

        Employee employee = entityMapper.toEmployeeEntity(employeeDTO, organization);
        String plainPassword = employee.getPassword();

        employee.setVerificationStatus(VerificationStatus.PENDING);

        if (plainPassword != null && !plainPassword.isEmpty()) {
            employee.setPassword(passwordEncoder.encode(plainPassword));
        }

        Employee saved = employeeRepository.save(employee); // ✅ cascade saves salaryStructure automatically

        // Send pending verification email
        if (plainPassword != null && !plainPassword.isEmpty()) {
            String emailBody = String.format(
                    "Dear %s,\n\n" +
                            "Your account has been created and is pending verification.\n\n" +
                            "Username: %s\n" +
                            "Email: %s\n" +
                            "Password: %s\n\n" +
                            "You will be notified once verification is complete.\n\n" +
                            "Regards,\nPayroll Management System",
                    saved.getFullName(), saved.getUsername(), saved.getEmail(), plainPassword);
            emailService.sendEmail(saved.getEmail(), "Account Pending Verification", emailBody);
        }

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
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        VerificationStatus verificationStatus;
        try {
            verificationStatus = VerificationStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Invalid status. Must be APPROVED or REJECTED");
        }

        employee.setVerificationStatus(verificationStatus);

        // 🔸 If approved
        if (verificationStatus == VerificationStatus.APPROVED) {
            String currentPassword = employee.getPassword();
            String plainPassword;

            // detect if existing password is encoded
            if (currentPassword == null || currentPassword.startsWith("$2a$") || currentPassword.startsWith("$2b$") || currentPassword.startsWith("$2y$")) {
                // Generate a temporary random password
                plainPassword = generateRandomPassword(10);
                employee.setPassword(passwordEncoder.encode(plainPassword));
            } else {
                // if somehow plain password stored, use it
                plainPassword = currentPassword;
                employee.setPassword(passwordEncoder.encode(plainPassword));
            }

            // Create user if not exists
            Optional<User> existingUser = userRepository.findByUsername(employee.getUsername());
            if (existingUser.isEmpty()) {
                User user = new User();
                user.setUsername(employee.getUsername());
                user.setEmail(employee.getEmail());
                user.setPassword(employee.getPassword()); // encoded
                user.setRole("ROLE_EMPLOYEE");
                user.setOrganization(employee.getOrganization());
                userRepository.save(user);
            }

            // Send approval email with plain password
            String emailBody = String.format(
                    "Dear %s,\n\n" +
                            "Your account has been approved successfully.\n\n" +
                            "Username: %s\n" +
                            "Email: %s\n" +
                            "Temporary Password: %s\n\n" +
                            "Please change your password after your first login.\n\n" +
                            "Regards,\nPayroll Management System",
                    employee.getFullName(), employee.getUsername(), employee.getEmail(), plainPassword);

            emailService.sendEmail(employee.getEmail(), "Employee Verification Approved", emailBody);
        }
// 🔸 If rejected
        else if (verificationStatus == VerificationStatus.REJECTED) {
            String emailBody = String.format(
                    "Dear %s,\n\n" +
                            "Your account verification has been rejected.\n\n" +
                            "Remarks: %s\n" +
                            "Status: %s\n\n" +
                            "Regards,\nPayroll Management System",
                    employee.getFullName(),
                    remarks != null ? remarks : "No remarks provided",
                    verificationStatus.name());
            emailService.sendEmail(employee.getEmail(), "Employee Verification Rejected", emailBody);
        }

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

    @Override
    public List<EmployeeDTO> getPendingVerifications() {
        List<Employee> pendingEmployees = employeeRepository.findByVerificationStatus(VerificationStatus.PENDING);

        return pendingEmployees.stream()
                .map(emp -> EmployeeDTO.builder()
                        .id(emp.getId())
                        .fullName(emp.getFullName())
                        .email(emp.getEmail())
                        .department(emp.getDepartment())
                        .organizationId(emp.getOrganization().getId())
                        .verificationStatus(emp.getVerificationStatus().name()) // <- use verificationStatus
                        .build())
                .collect(Collectors.toList());
    }
    
    private String generateRandomPassword(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
//    @Override
//    public EmployeeDTO changePassword(String username, String oldPassword, String newPassword) {
//        // 1️⃣ Find employee by username
//        Employee employee = employeeRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("Employee not found"));
//
//        // 2️⃣ Verify old password
//        if (!passwordEncoder.matches(oldPassword, employee.getPassword())) {
//            throw new RuntimeException("Old password is incorrect");
//        }
//
//        // 3️⃣ Encode new password and save
//        employee.setPassword(passwordEncoder.encode(newPassword));
//        employeeRepository.save(employee);
//
//        // 4️⃣ Optional: send email notification
//        String emailBody = String.format(
//                "Dear %s,\n\n" +
//                "Your password has been changed successfully.\n\n" +
//                "If you did not request this change, please contact the admin immediately.\n\n" +
//                "Regards,\nPayroll Management System",
//                employee.getFullName()
//        );
//        emailService.sendEmail(employee.getEmail(), "Password Changed", emailBody);
//
//        return entityMapper.toEmployeeDTO(employee);
//    }
    
    @Override
    public EmployeeDTO changePassword(String username, String oldPassword, String newPassword) {
        // 1️⃣ Find employee by username
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // 2️⃣ Verify old password
        if (!passwordEncoder.matches(oldPassword, employee.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // 3️⃣ Encode new password using the same encoder as Spring Security
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        employee.setPassword(encodedNewPassword);
        employeeRepository.save(employee);

        // 4️⃣ Optional: send email notification
        String emailBody = String.format(
                "Dear %s,\n\n" +
                "Your password has been changed successfully.\n\n" +
                "If you did not request this change, please contact the admin immediately.\n\n" +
                "Regards,\nPayroll Management System",
                employee.getFullName()
        );
        emailService.sendEmail(employee.getEmail(), "Password Changed", emailBody);

        // 5️⃣ Return updated EmployeeDTO
        return entityMapper.toEmployeeDTO(employee);
    }

    
    @Override
    public List<EmployeeDTO> getPendingVerifications(String adminUsername) {
        BankAdmin bankAdmin = bankAdminRepository.findByEmail(adminUsername)
            .or(() -> bankAdminRepository.findByUser_Username(adminUsername))
            .orElseThrow(() -> new RuntimeException("Bank admin not found"));

        Long bankId = bankAdmin.getBank().getId();

        List<Employee> pendingEmployees = employeeRepository.findByBankAndVerificationStatus(bankId, VerificationStatus.PENDING);

        return pendingEmployees.stream()
                .map(emp -> EmployeeDTO.builder()
                        .id(emp.getId())
                        .fullName(emp.getFullName())
                        .email(emp.getEmail())
                        .department(emp.getDepartment())
                        .organizationId(emp.getOrganization().getId())
                        .verificationStatus(emp.getVerificationStatus().name())
                        .build())
                .toList();
    }

    @Override
    public EmployeeDTO updateOwnProfile(String username, EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        boolean updated = false;

        if (employeeDTO.getFullName() != null && !employeeDTO.getFullName().isBlank()) {
            employee.setFullName(employeeDTO.getFullName());
            updated = true;
        }

        if (employeeDTO.getEmail() != null && !employeeDTO.getEmail().isBlank()) {
            employee.setEmail(employeeDTO.getEmail());
            updated = true;
        }

        if (!updated) {
            throw new RuntimeException("No updatable fields provided (only fullName or email allowed).");
        }

        employeeRepository.save(employee);
        return entityMapper.toEmployeeDTO(employee);
    }

    
}
