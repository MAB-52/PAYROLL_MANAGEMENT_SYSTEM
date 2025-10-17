package com.project.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.dto.ConcernDTO;
import com.project.dto.ConcernStatusUpdateRequest;
import com.project.entity.Concern;
import com.project.service.ConcernService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/concerns")
@RequiredArgsConstructor
public class ConcernController {

    private final ConcernService concernService;

    // Create a new concern and return DTO
    @PostMapping
    public ResponseEntity<ConcernDTO> createConcern(@RequestBody Concern concern) {
        ConcernDTO dto = concernService.createConcern(concern);
        return ResponseEntity.ok(dto);
    }

    // Get concerns by employee as DTO list
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ConcernDTO>> getConcernsByEmployee(@PathVariable Long employeeId) {
        List<ConcernDTO> dtos = concernService.getConcernsByEmployee(employeeId)
                .stream()
                .map(concern -> mapToDTO(concern))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Get all concerns as DTO list
    @GetMapping
    public ResponseEntity<List<ConcernDTO>> getAllConcerns() {
        List<ConcernDTO> dtos = concernService.getAllConcerns()
                .stream()
                .map(concern -> mapToDTO(concern))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Update concern status and return DTO
    @PutMapping("/{id}/status")
    public ResponseEntity<ConcernDTO> updateConcernStatus(
            @PathVariable Long id,
            @RequestBody ConcernStatusUpdateRequest request) {

        Concern concern = concernService.updateConcernStatus(
                id,
                request.getStatus(),
                request.getResolutionRemarks()
        );

        ConcernDTO dto = ConcernDTO.builder()
                .id(concern.getId())
                .subject(concern.getSubject())
                .message(concern.getMessage())
                .status(concern.getStatus().name())
                .raisedByEmployeeName(concern.getRaisedByEmployee().getFullName())
                .raisedByEmployeeEmail(concern.getRaisedByEmployee().getEmail())
                .createdAt(concern.getCreatedAt())
                .resolvedAt(concern.getResolvedAt())
                .resolutionRemarks(concern.getResolutionRemarks())
                .build();

        return ResponseEntity.ok(dto);
    }

    // Delete a concern
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConcern(@PathVariable Long id) {
        concernService.deleteConcern(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------------------
    // Helper method to convert Concern → ConcernDTO
    // ---------------------------
    private ConcernDTO mapToDTO(Concern concern) {
        return ConcernDTO.builder()
                .id(concern.getId())
                .subject(concern.getSubject())
                .message(concern.getMessage())
                .status(concern.getStatus().name())
                .raisedByEmployeeName(concern.getRaisedByEmployee() != null ? concern.getRaisedByEmployee().getFullName() : null)
                .raisedByEmployeeEmail(concern.getRaisedByEmployee() != null ? concern.getRaisedByEmployee().getEmail() : null)
                .createdAt(concern.getCreatedAt())
                .resolvedAt(concern.getResolvedAt())
                .resolutionRemarks(concern.getResolutionRemarks())
                .build();
    }
}
