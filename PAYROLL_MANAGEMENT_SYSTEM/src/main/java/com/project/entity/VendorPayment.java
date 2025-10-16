package com.project.entity;

import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private ClientVendor vendor;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SalaryStatus status; // PENDING, COMPLETED, FAILED

    private String remarks; // optional

    @ManyToOne
    @JoinColumn(name = "verified_by_admin_id")
    private BankAdmin verifiedBy;

    @Column(nullable = false)
    private LocalDateTime requestDate;
}
