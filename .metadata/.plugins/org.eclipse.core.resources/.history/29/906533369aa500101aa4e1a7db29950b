package com.project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "bank_admin")
public class BankAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String contactNumber;

    // ✅ Backward side of OneToOne with Bank
    @OneToOne
    @JoinColumn(name = "bank_id")
    @JsonBackReference // prevents recursion
    private Bank bank;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "verifiedBy", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SalaryPayment> verifiedPayments;

    @OneToMany(mappedBy = "handledBy", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Concern> handledConcerns;

    @OneToMany(mappedBy = "verifiedBy", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Organization> verifiedOrganizations;
}
