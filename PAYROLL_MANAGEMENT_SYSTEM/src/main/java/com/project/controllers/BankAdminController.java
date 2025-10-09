package com.project.controllers;

import com.project.entity.BankAdmin;
import com.project.entity.Concern;
import com.project.entity.Organization;
import com.project.entity.SalaryPayment;
import com.project.service.BankAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class BankAdminController {

    private final BankAdminService bankAdminService;

    // --- BankAdmin CRUD ---
    @PostMapping
    public ResponseEntity<BankAdmin> createAdmin(@RequestBody BankAdmin admin) {
        return ResponseEntity.ok(bankAdminService.createBankAdmin(admin));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankAdmin> getAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(bankAdminService.getBankAdminById(id));
    }

    @GetMapping
    public ResponseEntity<List<BankAdmin>> getAllAdmins() {
        return ResponseEntity.ok(bankAdminService.getAllBankAdmins());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BankAdmin> updateAdmin(@PathVariable Long id, @RequestBody BankAdmin updatedAdmin) {
        return ResponseEntity.ok(bankAdminService.updateBankAdmin(id, updatedAdmin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        bankAdminService.deleteBankAdmin(id);
        return ResponseEntity.noContent().build();
    }

    // --- Approvals ---
    @PostMapping("/organization/{orgId}/approve")
    public ResponseEntity<Organization> approveOrganization(@PathVariable Long orgId, @RequestParam Long adminId) {
        return ResponseEntity.ok(bankAdminService.approveOrganization(orgId, adminId));
    }

    @PostMapping("/organization/{orgId}/reject")
    public ResponseEntity<Organization> rejectOrganization(@PathVariable Long orgId,
                                                           @RequestParam Long adminId,
                                                           @RequestParam String remarks) {
        return ResponseEntity.ok(bankAdminService.rejectOrganization(orgId, adminId, remarks));
    }

    @PostMapping("/salary/{paymentId}/approve")
    public ResponseEntity<SalaryPayment> approveSalary(@PathVariable Long paymentId,
                                                       @RequestParam Long adminId) {
        return ResponseEntity.ok(bankAdminService.approveSalaryPayment(paymentId, adminId));
    }

    @PostMapping("/salary/{paymentId}/reject")
    public ResponseEntity<SalaryPayment> rejectSalary(@PathVariable Long paymentId,
                                                      @RequestParam Long adminId,
                                                      @RequestParam String remarks) {
        return ResponseEntity.ok(bankAdminService.rejectSalaryPayment(paymentId, adminId, remarks));
    }

    @PostMapping("/concern/{concernId}/resolve")
    public ResponseEntity<Concern> resolveConcern(@PathVariable Long concernId,
                                                  @RequestParam Long adminId,
                                                  @RequestParam String remarks) {
        return ResponseEntity.ok(bankAdminService.resolveConcern(concernId, adminId, remarks));
    }

    @PostMapping("/concern/{concernId}/reject")
    public ResponseEntity<Concern> rejectConcern(@PathVariable Long concernId,
                                                 @RequestParam Long adminId,
                                                 @RequestParam String remarks) {
        return ResponseEntity.ok(bankAdminService.rejectConcern(concernId, adminId, remarks));
    }
}
