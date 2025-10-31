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
@Table(name = "payment_requests")
public class PaymentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 💰 Amount of the payment
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @Column(nullable = false)
    private Double amount;

    // 📝 Description or purpose of the payment
    @NotBlank(message = "Description is required")
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    @Column(nullable = false, length = 200)
    private String description;

    // 🚦 Status of the request (PENDING, APPROVED, REJECTED)
    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // 🕒 Date of request creation
    @NotNull(message = "Created date is required")
    @PastOrPresent(message = "Created date cannot be in the future")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // ✅ Date of approval/rejection
    @PastOrPresent(message = "Verified date cannot be in the future")
    private LocalDateTime verifiedAt;

    // 🏢 Organization that raised the payment request
    @NotNull(message = "Organization is required")
    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    // 🏦 Bank admin who verified the payment (optional)
    @ManyToOne
    @JoinColumn(name = "verified_by_admin_id")
    private BankAdmin verifiedBy;
}
