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
@Table(name = "concerns")
public class Concern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🏷 Concern title — cannot be blank, limited to 100 chars
    @NotBlank(message = "Subject is required")
    @Size(max = 100, message = "Subject cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String subject;

    // 📄 Detailed message — cannot be blank
    @NotBlank(message = "Message cannot be empty")
    @Size(max = 2000, message = "Message cannot exceed 2000 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    // 🚦 Status of the concern
    @NotNull(message = "Concern status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConcernStatus status;

    // 🙋 Who raised the concern — either employee or organization
    @ManyToOne
    @JoinColumn(name = "raised_by_employee_id")
    private Employee raisedByEmployee;

    @ManyToOne
    @JoinColumn(name = "raised_by_organization_id")
    private Organization raisedByOrganization;

    // 🏦 Bank admin handling the concern
    @ManyToOne
    @JoinColumn(name = "handled_by_admin_id")
    private BankAdmin handledBy;

    // 🕒 Timestamps
    @PastOrPresent(message = "Created time cannot be in the future")
    private LocalDateTime createdAt;

    @PastOrPresent(message = "Resolved time cannot be in the future")
    private LocalDateTime resolvedAt;

    // 📝 Optional remarks for resolution
    @Size(max = 500, message = "Resolution remarks cannot exceed 500 characters")
    private String resolutionRemarks;
}
