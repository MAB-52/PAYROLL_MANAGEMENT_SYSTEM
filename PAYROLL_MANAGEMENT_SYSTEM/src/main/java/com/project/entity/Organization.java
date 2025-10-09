package com.project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @Column(nullable = false)
    private String orgName;

    @Column(unique = true, nullable = false)
    private String registrationNumber;

    @Column(nullable = false)
    private String contactEmail;

    private String contactPhone;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;  // PENDING, APPROVED, REJECTED

    private String documentUrl;

    private String address;

    // ✅ Many organizations belong to one Bank
    @ManyToOne
    @JoinColumn(name = "bank_id", nullable = false)
    @JsonBackReference // prevents infinite loop
    private Bank bank;

    @ManyToOne
    @JoinColumn(name = "bank_admin_id")
    @JsonIgnore // prevent nested admin inside org
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
