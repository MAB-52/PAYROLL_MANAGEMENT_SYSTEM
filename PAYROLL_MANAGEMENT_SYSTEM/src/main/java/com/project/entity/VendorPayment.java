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
@Table(name = "vendor_payments")
public class VendorPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Vendor making the payment request
    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    @NotNull(message = "Vendor is required")
    private ClientVendor vendor;

    // Organization related to the payment
    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    @NotNull(message = "Organization is required")
    private Organization organization;

    // Payment amount (must be positive)
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @Column(nullable = false)
    private Double amount;

    // Payment status (PENDING, COMPLETED, FAILED)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Payment status is required")
    @Column(nullable = false)
    private SalaryStatus status;

    // Optional remarks
    @Column(nullable = true)
    private String remarks;

    // Bank admin who verified the payment
    @ManyToOne
    @JoinColumn(name = "verified_by_admin_id")
    private BankAdmin verifiedBy;

    // Date when the payment request was made
    @NotNull(message = "Request date is required")
    @Column(nullable = false)
    private LocalDateTime requestDate;
}
