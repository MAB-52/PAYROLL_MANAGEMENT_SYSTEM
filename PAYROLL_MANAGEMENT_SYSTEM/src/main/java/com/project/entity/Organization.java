package com.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🏢 Organization name
    @NotBlank(message = "Organization name is required")
    @Size(min = 3, max = 100, message = "Organization name must be between 3 and 100 characters")
    @Column(nullable = false)
    private String orgName;

    // 🆔 Unique registration number
    @NotBlank(message = "Registration number is required")
    @Size(min = 5, max = 50, message = "Registration number must be between 5 and 50 characters")
    @Column(unique = true, nullable = false)
    private String registrationNumber;

    // 📧 Contact email
    @Email(message = "Invalid email format")
    @NotBlank(message = "Contact email is required")
    @Column(nullable = false)
    private String contactEmail;

    // ☎️ Contact phone number (10 digits starting with 6–9)
    @NotBlank(message = "Contact phone is required")
    @Pattern(
        regexp = "^[6-9]\\d{9}$",
        message = "Contact phone must be a valid 10-digit Indian number starting with 6-9"
    )
    @Column(nullable = false)
    private String contactPhone;

    // 📜 Verification status (e.g., PENDING, APPROVED, REJECTED)
    @NotNull(message = "Verification status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus;

    // 📄 Document URL
    @NotBlank(message = "Document URL is required")
    @Pattern(
        regexp = "^(https?:\\/\\/)?([\\w.-]+)\\.([a-z]{2,6}\\.?)([\\/\\w .-]*)*\\/?$",
        message = "Invalid document URL format"
    )
    @Column(nullable = false)
    private String documentUrl;

    // 🏠 Address
    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
    @Column(nullable = false)
    private String address;

    // 🏦 Associated bank (required)
    @ManyToOne
    @JoinColumn(name = "bank_id", nullable = false)
    @NotNull(message = "Bank reference is required")
    @JsonBackReference
    private Bank bank;

    // 👨‍💼 Verified by Bank Admin (nullable — only set after approval)
    @ManyToOne
    @JoinColumn(name = "bank_admin_id")
    @JsonIgnore
    private BankAdmin verifiedBy;

    // 👷 Employees in this organization
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Employee> employees;

    // 🧾 Vendors and Clients
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ClientVendor> clientVendors;

    // 💸 Payment Requests made by the organization
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PaymentRequest> paymentRequests;

    // ⚠️ Concerns raised by this organization
    @OneToMany(mappedBy = "raisedByOrganization", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Concern> concerns;

    // 👥 Users associated with this organization
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<User> users;
}
