package com.project.controllers;

import com.project.entity.Bank;
import com.project.repo.BankRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankRepo bankRepo;

    @PostMapping
    public ResponseEntity<Bank> createBank(@RequestBody Bank bank) {
        return ResponseEntity.ok(bankRepo.save(bank));
    }

    @GetMapping
    public ResponseEntity<List<Bank>> getAllBanks() {
        return ResponseEntity.ok(bankRepo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bank> getBank(@PathVariable Long id) {
        return ResponseEntity.ok(bankRepo.findById(id).orElseThrow());
    }
}
