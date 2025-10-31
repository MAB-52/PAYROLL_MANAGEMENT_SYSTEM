package com.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "salary_payments")
public class SalaryPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 💰 Amount paid as salary
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be a positive value")
    @Column(nullable = false)
    private Double amount;

    // 📅 Payment date
    @NotNull(message = "Payment date is required")
    @PastOrPresent(message = "Payment date cannot be in the future")
    @Column(nullable = false)
    private LocalDateTime paymentDate;

    // 🗓️ Month or period of salary
    @NotBlank(message = "Salary month is required")
    @Pattern(
        regexp = "^(0[1-9]|1[0-2])-(19|20)\\d{2}$",
        message = "Salary month must be in MM-YYYY format (e.g. 09-2025)"
    )
    @Column(nullable = false)
    private String salaryMonth;

    // 📦 Payment status (e.g. PENDING, COMPLETED, FAILED)
    @NotNull(message = "Salary status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SalaryStatus status;

    // 📝 Remarks (optional)
    @Size(max = 255, message = "Remarks cannot exceed 255 characters")
    @Column(nullable = true)
    private String remarks;

    // 👤 Employee receiving the salary
    @NotNull(message = "Employee reference is required")
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // 🏢 Organization making the payment
    @NotNull(message = "Organization reference is required")
    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    // 🏦 Optional verification or approval by bank admin
    @ManyToOne
    @JoinColumn(name = "verified_by_admin_id")
    private BankAdmin verifiedBy;
}
