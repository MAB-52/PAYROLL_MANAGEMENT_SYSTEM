package com.project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "concerns")
public class Concern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Concern title
    @Column(nullable = false)
    private String subject;

    // Detailed message
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    // Status of the concern
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConcernStatus status;

    // Who raised the concern (employee or organization)
    @ManyToOne
    @JoinColumn(name = "raised_by_employee_id")
    private Employee raisedByEmployee;

    @ManyToOne
    @JoinColumn(name = "raised_by_organization_id")
    private Organization raisedByOrganization;

    // Bank admin who handles or resolves the concern
    @ManyToOne
    @JoinColumn(name = "handled_by_admin_id")
    private BankAdmin handledBy;

    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    private String resolutionRemarks;
}
