package com.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "organization")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Organization name is required")
    @Size(min = 3, message = "Organization name must have at least 3 characters")
    private String orgName;

    @NotBlank(message = "Registration number is required")
    @Column(unique = true, nullable = false)
    private String registrationNumber;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Contact email is required")
    @Column(nullable = false)
    private String contactEmail;

    @NotBlank(message = "Contact phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number format")
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Verification status is required")
    private VerificationStatus verificationStatus;

    @NotBlank(message = "Document URL is required")
    @Pattern(regexp = "^(http|https)://.*$", message = "Invalid document URL format")
    private String documentUrl;

    @NotBlank(message = "Address is required")
    private String address;

    @ManyToOne
    @JoinColumn(name = "bank_id", nullable = false)
    @NotNull(message = "Bank reference is required")
    @JsonBackReference
    private Bank bank;

    @ManyToOne
    @JoinColumn(name = "bank_admin_id")
    @JsonIgnore
    private BankAdmin verifiedBy;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Employee> employees;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ClientVendor> clientVendors;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PaymentRequest> paymentRequests;

    @OneToMany(mappedBy = "raisedByOrganization", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Concern> concerns;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<User> users;
}
