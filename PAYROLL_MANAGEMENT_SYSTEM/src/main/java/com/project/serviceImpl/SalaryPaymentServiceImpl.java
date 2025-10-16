package com.project.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.dto.SalaryPaymentDTO;
import com.project.entity.BankAdmin;
import com.project.entity.Employee;
import com.project.entity.Organization;
import com.project.entity.SalaryPayment;
import com.project.entity.SalaryStatus;
import com.project.entity.VerificationStatus;
import com.project.repo.BankAdminRepo;
import com.project.repo.EmployeeRepo;
import com.project.repo.OrganizationRepo;
import com.project.repo.SalaryPaymentRepo;
import com.project.repo.UserRepo;
import com.project.service.SalaryPaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalaryPaymentServiceImpl implements SalaryPaymentService {

    private final SalaryPaymentRepo salaryPaymentRepo;
    private final EmployeeRepo employeeRepo;
    private final OrganizationRepo organizationRepo;
    private final UserRepo userRepo;
    private final BankAdminRepo bankAdminRepo;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    @Override
    public List<SalaryPaymentDTO> createSalaryDisbursal(Long organizationId, String month) {
        Organization org = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        List<Employee> employees = employeeRepo.findByOrganizationId(organizationId)
                .stream()
                .filter(e -> e.getVerificationStatus() == VerificationStatus.APPROVED)
                .collect(Collectors.toList());

        List<SalaryPayment> payments = employees.stream()
                .map(e -> SalaryPayment.builder()
                        .employee(e)
                        .organization(org)
                        .salaryMonth(month)
                        .amount(e.getSalaryStructure().getNetSalary())
                        .paymentDate(LocalDateTime.now())
                        .status(SalaryStatus.PENDING)
                        .build())
                .collect(Collectors.toList());

        salaryPaymentRepo.saveAll(payments);

        return payments.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public SalaryPaymentDTO approveSalaryPayment(Long paymentId, Long adminId) {
        SalaryPayment payment = salaryPaymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        BankAdmin admin = bankAdminRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Bank Admin not found"));

        // Execute batch processing asynchronously
        executor.submit(() -> {
            payment.setStatus(SalaryStatus.COMPLETED);
            payment.setVerifiedBy(admin);
            salaryPaymentRepo.save(payment);
            // TODO: integrate actual bank API transfer if needed
        });

        return toDTO(payment);
    }

    @Override
    public SalaryPaymentDTO rejectSalaryPayment(Long paymentId, Long adminId, String remarks) {
        SalaryPayment payment = salaryPaymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        BankAdmin admin = bankAdminRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Bank Admin not found"));

        payment.setStatus(SalaryStatus.FAILED);
        payment.setVerifiedBy(admin);
        // Optionally store remarks somewhere
        salaryPaymentRepo.save(payment);

        return toDTO(payment);
    }

    @Override
    public List<SalaryPaymentDTO> getPaymentsByOrganization(Long organizationId) {
        return salaryPaymentRepo.findByOrganizationId(organizationId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private SalaryPaymentDTO toDTO(SalaryPayment payment) {
        return SalaryPaymentDTO.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .salaryMonth(payment.getSalaryMonth())
                .paymentDate(payment.getPaymentDate())
                .status(payment.getStatus().name())
                .employeeId(payment.getEmployee().getId())
                .organizationId(payment.getOrganization().getId())
                .verifiedByAdminId(payment.getVerifiedBy() != null ? payment.getVerifiedBy().getId() : null)
                .build();
    }
}
