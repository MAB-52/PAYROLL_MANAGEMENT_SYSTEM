package com.project.controllers;

import com.project.entity.Concern;
import com.project.service.ConcernService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/concerns")
@RequiredArgsConstructor
public class ConcernController {

    private final ConcernService concernService;

    @PostMapping
    public ResponseEntity<Concern> createConcern(@RequestBody Concern concern) {
        return ResponseEntity.ok(concernService.createConcern(concern));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Concern>> getConcernsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(concernService.getConcernsByEmployee(employeeId));
    }

    @GetMapping
    public ResponseEntity<List<Concern>> getAllConcerns() {
        return ResponseEntity.ok(concernService.getAllConcerns());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Concern> updateConcernStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(concernService.updateConcernStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConcern(@PathVariable Long id) {
        concernService.deleteConcern(id);
        return ResponseEntity.noContent().build();
    }
}
