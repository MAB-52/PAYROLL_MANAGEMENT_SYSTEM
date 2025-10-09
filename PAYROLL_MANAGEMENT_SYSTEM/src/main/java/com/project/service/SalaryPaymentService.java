package com.project.service;

import com.project.entity.SalaryPayment;
import java.util.List;

public interface SalaryPaymentService {
    SalaryPayment createPayment(SalaryPayment payment);
    List<SalaryPayment> getPaymentsByEmployee(Long employeeId);
    List<SalaryPayment> getAllPayments();
    SalaryPayment updatePaymentStatus(Long id, String status);
    void deletePayment(Long id);
}
