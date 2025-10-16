package com.project.serviceImpl;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.dto.LoginRequestDTO;
import com.project.dto.LoginResponseDTO;
import com.project.dto.OrganizationDTO;
import com.project.dto.RegisterRequestDTO;
import com.project.dto.RegisterResponseDTO;
import com.project.entity.Bank;
import com.project.entity.BankAdmin;
import com.project.entity.Employee;
import com.project.entity.Organization;
import com.project.entity.User;
import com.project.entity.VerificationStatus;
import com.project.mapper.EntityMapper;
import com.project.repo.BankRepo;
import com.project.repo.EmployeeRepo;
import com.project.repo.OrganizationRepo;
import com.project.repo.UserRepo;
import com.project.security.JwtUtil;
import com.project.service.AuthService;
import com.project.service.BankAdminService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final EmployeeRepo employeeRepo;
    private final JwtUtil jwtUtil;
    private final BankAdminService bankAdminService;
    private final OrganizationRepo organizationRepo;
    private final BankRepo bankRepo;
    private final EntityMapper entityMapper;

    // ==================================
    // 🔹 LOGIN (for all roles)
    // ==================================
    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        String username = request.getUsername();
        String password = request.getPassword();

        // 1️⃣ Check in User table (Bank Admin, Manager, etc.)
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password)
                );
            } catch (Exception ex) {
                throw new RuntimeException("Invalid username or password");
            }

            // generate token with role
            String token = jwtUtil.generateToken(username, user.getRole());

            return LoginResponseDTO.builder()
                    .token(token)
                    .role(user.getRole())
                    .username(username)
                    .build();
        }

        // 2️⃣ Check in Employee table
        Optional<Employee> empOpt = employeeRepo.findByUsername(username);
        if (empOpt.isPresent()) {
            Employee emp = empOpt.get();

            if (!passwordEncoder.matches(password, emp.getPassword())) {
                throw new RuntimeException("Invalid username or password");
            }

            if (emp.getVerificationStatus() != VerificationStatus.APPROVED) {
                throw new RuntimeException("Employee not approved by Bank Admin yet");
            }

            String token = jwtUtil.generateToken(username, "ROLE_EMPLOYEE");

            return LoginResponseDTO.builder()
                    .token(token)
                    .role("ROLE_EMPLOYEE")
                    .username(username)
                    .build();
        }

        throw new RuntimeException("Invalid username or password");
    }

    // ==================================
    // 🔹 REGISTER BANK ADMIN
    // ==================================
    @Override
    public RegisterResponseDTO registerBankAdmin(RegisterRequestDTO request) {
        String username = request.getEmail().split("@")[0];

        BankAdmin admin = BankAdmin.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .contactNumber(request.getContactNumber())
                .build();

        bankAdminService.createBankAdmin(admin);

        return RegisterResponseDTO.builder()
                .message("Bank Admin registered successfully")
                .username(username)
                .role("ROLE_BANK_ADMIN")
                .build();
    }

    // ==================================
    // 🔹 REGISTER ORGANIZATION (Manager)
    // ==================================
    @Override
    public RegisterResponseDTO registerOrganization(RegisterRequestDTO request) {
        Bank bank = bankRepo.findById(request.getBankId())
                .orElseThrow(() -> new RuntimeException("Bank not found with ID: " + request.getBankId()));

        OrganizationDTO orgDto = OrganizationDTO.builder()
                .orgName(request.getOrgName())
                .registrationNumber(request.getRegistrationNumber())
                .email(request.getEmail())
                .contactNumber(request.getContactNumber())
                .address(request.getAddress())
                .documentUrl(request.getDocumentUrl())
                .bankId(request.getBankId())
                .build();

        Organization organization = entityMapper.toOrganizationEntity(orgDto, bank);
        organization.setVerificationStatus(VerificationStatus.PENDING);

        Organization savedOrg = organizationRepo.save(organization);

        return RegisterResponseDTO.builder()
                .message("Organization registered successfully")
                .role("ROLE_MANAGER")
                .username(savedOrg.getContactEmail())
                .email(savedOrg.getContactEmail())
                .build();
    }
}
