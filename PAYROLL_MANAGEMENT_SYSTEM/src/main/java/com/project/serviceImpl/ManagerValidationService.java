package com.project.serviceImpl;

import com.project.entity.User;
import com.project.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagerValidationService {

    private final UserRepo userRepo;

    public void validateManagerInOrganization(String username, Long orgId) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Ensure ROLE_MANAGER
        if (!"ROLE_MANAGER".equals(user.getRole())) {
            throw new RuntimeException("Access Denied! Only Managers can create salary requests.");
        }

        // Ensure he belongs to the same organization
        if (user.getOrganization() == null || !user.getOrganization().getId().equals(orgId)) {
            throw new RuntimeException("You are not the Manager of this Organization!");
        }
    }
}
