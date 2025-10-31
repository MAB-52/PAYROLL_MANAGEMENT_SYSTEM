package com.project.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.entity.ClientVendor;
import com.project.service.ClientVendorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class ClientVendorController {

    private final ClientVendorService clientVendorService;

    @PostMapping("/{orgId}/vendor")
    public ResponseEntity<ClientVendor> addVendor(
    			@PathVariable Long orgId,
    		@Valid	@RequestBody ClientVendor vendor) {
        ClientVendor createdVendor = clientVendorService.createVendor(orgId, vendor);
        return ResponseEntity.ok(createdVendor);
    }

    @GetMapping("/{orgId}/vendors")
    public ResponseEntity<List<ClientVendor>> getVendors(
    		@Valid	@PathVariable Long orgId) {
        List<ClientVendor> vendors = clientVendorService.getVendorsByOrganization(orgId);
        return ResponseEntity.ok(vendors);
    }
}
