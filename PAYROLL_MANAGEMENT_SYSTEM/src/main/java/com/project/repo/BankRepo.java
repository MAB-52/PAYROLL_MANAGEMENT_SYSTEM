package com.project.repo;

import com.project.entity.Bank;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepo extends JpaRepository<Bank, Long> {
	Optional<Bank> findByBankName(String bankName);
}
