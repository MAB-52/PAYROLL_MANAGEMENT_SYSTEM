package com.project.service;

import java.util.List;

import com.project.dto.OrganizationApprovalRequest;
import com.project.dto.OrganizationApprovalResponse;
import com.project.dto.SalaryPaymentDTO;
import com.project.dto.VendorPaymentDTO;
import com.project.entity.BankAdmin;
import com.project.entity.Concern;
import com.project.entity.Organization;

public interface BankAdminService {
	
    BankAdmin createBankAdmin(BankAdmin admin);

    BankAdmin getBankAdminById(Long id);

    List<BankAdmin> getAllBankAdmins();

    BankAdmin updateBankAdmin(Long id, BankAdmin updatedAdmin);

    void deleteBankAdmin(Long id);
    
    OrganizationApprovalResponse verifyOrganization(Long orgId, OrganizationApprovalRequest request);
    
    public List<Organization> getAllOrganizations();
    public Organization getOrganizationById(Long orgId);
    
    // --- Organization Approvals ---
    Organization approveOrganization(Long orgId, Long adminId);
    Organization rejectOrganization(Long orgId, Long adminId, String remarks);

    // --- Concern Resolution ---
    Concern resolveConcern(Long concernId, Long adminId, String resolutionRemarks);
    Concern rejectConcern(Long concernId, Long adminId, String remarks);
    
    List<SalaryPaymentDTO> createSalaryDisbursalRequest(Long organizationId, String month);

    // Approve salary payment (Bank Admin)
    SalaryPaymentDTO approveSalaryPayment(Long paymentId, Long adminId);

    // Reject salary payment (Bank Admin)
    SalaryPaymentDTO rejectSalaryPayment(Long paymentId, Long adminId, String remarks);
    
    List<SalaryPaymentDTO> approveSalaryBatch(Long organizationId, Long adminId, String month, boolean approved, String remarks);
    
    VendorPaymentDTO requestVendorPayment(Long orgId, Long vendorId, Double amount, String remarks);

    VendorPaymentDTO approveVendorPayment(Long paymentId, Long adminId);

    VendorPaymentDTO rejectVendorPayment(Long paymentId, Long adminId, String remarks);

}
