package com.project.repo;

import com.project.entity.BankAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAdminRepo extends JpaRepository<BankAdmin, Long> {
    Optional<BankAdmin> findByEmail(String email);
}
