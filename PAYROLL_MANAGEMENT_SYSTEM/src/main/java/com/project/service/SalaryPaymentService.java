package com.project.service;

import com.project.dto.SalaryPaymentDTO;
import com.project.entity.SalaryPayment;

import java.util.List;

public interface SalaryPaymentService {

    // Manager requests salary disbursal
    List<SalaryPaymentDTO> createSalaryDisbursal(Long organizationId, String month);

    // Bank admin approves salary
    SalaryPaymentDTO approveSalaryPayment(Long paymentId, Long adminId);

    // Bank admin rejects salary
    SalaryPaymentDTO rejectSalaryPayment(Long paymentId, Long adminId, String remarks);

    List<SalaryPaymentDTO> getPaymentsByOrganization(Long organizationId);
}
