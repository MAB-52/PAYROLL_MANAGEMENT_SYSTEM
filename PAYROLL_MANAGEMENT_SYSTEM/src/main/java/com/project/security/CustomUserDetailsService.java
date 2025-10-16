package com.project.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.repo.BankAdminRepo;
import com.project.repo.EmployeeRepo;
import com.project.repo.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepo employeeRepo;
    private final BankAdminRepo bankAdminRepo;
    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {

        // 1️⃣ MAIN USER TABLE (Primary Authentication Source)
        var userOpt = userRepo.findByUsername(input);
        if (userOpt.isPresent()) {
            var user = userOpt.get();

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(user.getRole())   // 🔥 USE DB ROLE DIRECTLY LIKE "ROLE_BANK_ADMIN"
                    .build();
        }

        // 2️⃣ BANK ADMIN (Using Linked User.username or fallback email)
        var adminOpt = bankAdminRepo.findByUser_Username(input);
        if (adminOpt.isPresent()) {
            var admin = adminOpt.get();
            String username = admin.getUser() != null ? admin.getUser().getUsername() : admin.getEmail();
            String password = admin.getUser() != null ? admin.getUser().getPassword() : admin.getPassword();

            return org.springframework.security.core.userdetails.User.builder()
                    .username(username)
                    .password(password)
                    .authorities("ROLE_BANK_ADMIN")
                    .build();
        }

        var adminByEmailOpt = bankAdminRepo.findByEmail(input);
        if (adminByEmailOpt.isPresent()) {
            var admin = adminByEmailOpt.get();
            String password = admin.getUser() != null ? admin.getUser().getPassword() : admin.getPassword();

            return org.springframework.security.core.userdetails.User.builder()
                    .username(admin.getEmail())
                    .password(password)
                    .authorities("ROLE_BANK_ADMIN")
                    .build();
        }

        // 3️⃣ EMPLOYEE Authentication (Email or Username)
        var empOpt = employeeRepo.findByEmail(input);
        if (empOpt.isPresent()) {
            var emp = empOpt.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(emp.getEmail())
                    .password(emp.getPassword())
                    .authorities("ROLE_EMPLOYEE")
                    .build();
        }

        var empByUsernameOpt = employeeRepo.findByUsername(input);
        if (empByUsernameOpt.isPresent()) {
            var emp = empByUsernameOpt.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(emp.getUsername())
                    .password(emp.getPassword())
                    .authorities("ROLE_EMPLOYEE")
                    .build();
        }

        // ❌ Nothing Found
        throw new UsernameNotFoundException("User not found with username/email: " + input);
    }
}
