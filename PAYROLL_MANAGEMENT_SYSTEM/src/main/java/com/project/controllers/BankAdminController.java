package com.project.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.dto.EmployeeDTO;
import com.project.dto.EmployeeStatusRequest;
import com.project.dto.OrganizationApprovalRequest;
import com.project.dto.OrganizationApprovalResponse;
import com.project.dto.SalaryPaymentDTO;
import com.project.dto.VendorPaymentDTO;
import com.project.entity.BankAdmin;
import com.project.entity.Concern;
import com.project.entity.Organization;
import com.project.service.BankAdminService;
import com.project.service.EmployeeService;
import com.project.service.OrganizationService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bank-admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BANK_ADMIN')")
public class BankAdminController {

    private final BankAdminService bankAdminService;
    private final OrganizationService organizationService;
    private final EmployeeService employeeService;

    // ───────────────────────────────
    // 🏦 Bank Admin CRUD
    // ───────────────────────────────
    @PostMapping
    public ResponseEntity<?> createAdmin(@RequestBody BankAdmin admin) {
        try {
            BankAdmin createdAdmin = bankAdminService.createBankAdmin(admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAdmin);
        } catch (RuntimeException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
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

    // ───────────────────────────────
    // 🏢 Organization Management
    // ───────────────────────────────
    @GetMapping("/organizations")
    public ResponseEntity<List<Organization>> getAllOrganizations() {
        return ResponseEntity.ok(organizationService.getAllOrganizations());
    }

    @GetMapping("/organizations/{orgId}")
    public ResponseEntity<Organization> getOrganizationById(@PathVariable Long orgId) {
        return ResponseEntity.ok(organizationService.getOrganizationById(orgId));
    }

    @PostMapping("/organization/{orgId}/approve")
    public ResponseEntity<Organization> approveOrganization(@PathVariable Long orgId,
                                                            @RequestParam Long adminId) {
        return ResponseEntity.ok(bankAdminService.approveOrganization(orgId, adminId));
    }

    @PostMapping("/organization/{orgId}/reject")
    public ResponseEntity<Organization> rejectOrganization(@PathVariable Long orgId,
                                                           @RequestParam Long adminId,
                                                           @RequestParam String remarks) {
        return ResponseEntity.ok(bankAdminService.rejectOrganization(orgId, adminId, remarks));
    }
    
    @PostMapping("/organization/{orgId}/verify")
    public ResponseEntity<?> verifyOrganization(@PathVariable Long orgId,
                                                @RequestBody OrganizationApprovalRequest request) {
        try {
            OrganizationApprovalResponse response = bankAdminService.verifyOrganization(orgId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    // ───────────────────────────────
    // 📩 Concern Resolution
    // ───────────────────────────────
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
    
    @PostMapping("/employees/{employeeId}/status")
    public ResponseEntity<EmployeeDTO> updateEmployeeStatus(
            @PathVariable Long employeeId,
            @RequestBody EmployeeStatusRequest request) {

        EmployeeDTO employee = employeeService.approveEmployeeStatus(
                employeeId,
                request.getStatus(),
                request.getRemarks()
        );
        return ResponseEntity.ok(employee);
    }

    // Request DTO
    @Data
    public static class SalaryDisbursalRequest {
        private String month;
    }

 // ───────────────────────────────
 // ✅ Approve Salary Payment (Bank Admin)
 // ───────────────────────────────
 @PostMapping("/salary/{paymentId}/approve")
 public ResponseEntity<SalaryPaymentDTO> approveSalary(
         @PathVariable Long paymentId,
         @RequestParam Long adminId) {

     SalaryPaymentDTO payment = bankAdminService.approveSalaryPayment(paymentId, adminId);
     return ResponseEntity.ok(payment);
 }

 // ───────────────────────────────
 // ❌ Reject Salary Payment (Bank Admin)
 // ───────────────────────────────
 @PostMapping("/salary/{paymentId}/reject")
 public ResponseEntity<SalaryPaymentDTO> rejectSalary(
         @PathVariable Long paymentId,
         @RequestParam Long adminId,
         @RequestParam String remarks) {

     SalaryPaymentDTO payment = bankAdminService.rejectSalaryPayment(paymentId, adminId, remarks);
     return ResponseEntity.ok(payment);
 }
 
//───────────────────────────────
//💰 Update Salary Payment Status (Approve / Reject)
//───────────────────────────────
@PostMapping("/salary/{paymentId}/status")
public ResponseEntity<SalaryPaymentDTO> updateSalaryStatus(
      @PathVariable Long paymentId,
      @RequestBody SalaryStatusRequest request
) {
  SalaryPaymentDTO payment;

  if (request.isApproved()) {
      payment = bankAdminService.approveSalaryPayment(paymentId, request.getAdminId());
  } else {
      payment = bankAdminService.rejectSalaryPayment(paymentId, request.getAdminId(), request.getRemarks());
  }

  return ResponseEntity.ok(payment);
}

//✅ Request DTO for updating salary status (approve/reject)
@Data
public static class SalaryStatusRequest {
 private boolean approved; // true = approve, false = reject
 private Long adminId;
 private String remarks; // optional for rejection
}



@PostMapping("/organization/{orgId}/salary/approve-batch")
public ResponseEntity<?> approveSalaryBatch(
        @PathVariable Long orgId,
        @RequestBody SalaryBatchRequest request) {

    try {
        List<SalaryPaymentDTO> result = bankAdminService.approveSalaryBatch(
                orgId,
                request.getAdminId(),
                request.getMonth(),
                request.isApproved(),
                request.getRemarks()
        );
        return ResponseEntity.ok(result);
    } catch (RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}

// Request DTO
@Data
public static class SalaryBatchRequest {
    private Long adminId;
    private String month;
    private boolean approved;
    private String remarks; // optional
}

@PostMapping("/vendor-payment/request")
public ResponseEntity<VendorPaymentDTO> requestVendorPayment(@RequestBody VendorPaymentRequest request) {
    VendorPaymentDTO result = bankAdminService.requestVendorPayment(
            request.getOrganizationId(),
            request.getVendorId(),
            request.getAmount(),
            request.getRemarks()
    );
    return ResponseEntity.ok(result);
}

@PostMapping("/vendor-payment/{paymentId}/approve")
public ResponseEntity<VendorPaymentDTO> approveVendorPayment(@PathVariable Long paymentId,
                                                             @RequestBody AdminActionRequest request) {
    return ResponseEntity.ok(bankAdminService.approveVendorPayment(paymentId, request.getAdminId()));
}

@PostMapping("/vendor-payment/{paymentId}/reject")
public ResponseEntity<VendorPaymentDTO> rejectVendorPayment(@PathVariable Long paymentId,
                                                            @RequestBody AdminActionRequest request) {
    return ResponseEntity.ok(bankAdminService.rejectVendorPayment(paymentId, request.getAdminId(), request.getRemarks()));
}

@PostMapping("/vendor-payment/{paymentId}/status")
public ResponseEntity<VendorPaymentDTO> updateVendorPaymentStatus(
        @PathVariable Long paymentId,
        @RequestBody VendorPaymentStatusRequest request
) {
    VendorPaymentDTO payment;

    if (request.isApprove()) {
        payment = bankAdminService.approveVendorPayment(paymentId, request.getAdminId());
    } else {
        payment = bankAdminService.rejectVendorPayment(paymentId, request.getAdminId(), request.getRemarks());
    }

    return ResponseEntity.ok(payment);
}

// DTO for request
@Data
public static class VendorPaymentStatusRequest {
    private boolean approve; // true = approve, false = reject
    private Long adminId;
    private String remarks; // optional for rejection
}


// Request DTOs
@Data
public static class VendorPaymentRequest {
    private Long organizationId;
    private Long vendorId;
    private Double amount;
    private String remarks;
}

@Data
public static class AdminActionRequest {
    private Long adminId;
    private String remarks; // optional
}



}
