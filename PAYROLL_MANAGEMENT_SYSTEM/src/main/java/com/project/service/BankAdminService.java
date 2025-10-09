package com.project.service;

import java.util.List;

import com.project.entity.BankAdmin;
import com.project.entity.Concern;
import com.project.entity.Organization;
import com.project.entity.SalaryPayment;

public interface BankAdminService {

    BankAdmin createBankAdmin(BankAdmin admin);

    BankAdmin getBankAdminById(Long id);

    List<BankAdmin> getAllBankAdmins();

    BankAdmin updateBankAdmin(Long id, BankAdmin updatedAdmin);

    void deleteBankAdmin(Long id);
    
    // --- Organization Approvals ---
    Organization approveOrganization(Long orgId, Long adminId);
    Organization rejectOrganization(Long orgId, Long adminId, String remarks);

    // --- Salary Payment Approvals ---
    SalaryPayment approveSalaryPayment(Long paymentId, Long adminId);
    SalaryPayment rejectSalaryPayment(Long paymentId, Long adminId, String remarks);

    // --- Concern Resolution ---
    Concern resolveConcern(Long concernId, Long adminId, String resolutionRemarks);
    Concern rejectConcern(Long concernId, Long adminId, String remarks);
}
