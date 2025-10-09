package com.project.controllers;

import com.project.entity.SalaryStructure;
import com.project.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    @PostMapping
    public ResponseEntity<SalaryStructure> createSalary(@RequestBody SalaryStructure salaryStructure) {
        return ResponseEntity.ok(salaryService.createSalary(salaryStructure));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaryStructure> getSalary(@PathVariable Long id) {
        return ResponseEntity.ok(salaryService.getSalaryById(id));
    }

    @GetMapping
    public ResponseEntity<List<SalaryStructure>> getAllSalaries() {
        return ResponseEntity.ok(salaryService.getAllSalaries());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalaryStructure> updateSalary(@PathVariable Long id,
                                                        @RequestBody SalaryStructure updatedSalary) {
        return ResponseEntity.ok(salaryService.updateSalary(id, updatedSalary));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalary(@PathVariable Long id) {
        salaryService.deleteSalary(id);
        return ResponseEntity.noContent().build();
    }
}
