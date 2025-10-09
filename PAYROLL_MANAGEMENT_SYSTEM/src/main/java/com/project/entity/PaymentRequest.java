package com.project.entity;

import jakarta.persistence.*;
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

    // Amount of the payment
    @Column(nullable = false)
    private Double amount;

    // Description or purpose of the payment
    private String description;

    // Status of the request (PENDING, APPROVED, REJECTED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // Date of request creation
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Date of approval/rejection
    private LocalDateTime verifiedAt;

    // Optional document path or file reference
    //private String documentPath;

    // Organization that raised the payment request
    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    // Bank admin who verified the payment
    @ManyToOne
    @JoinColumn(name = "verified_by_admin_id")
    private BankAdmin verifiedBy;
}
