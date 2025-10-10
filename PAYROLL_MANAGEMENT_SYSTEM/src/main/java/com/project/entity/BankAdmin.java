package com.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

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

    @NotBlank(message = "Admin name is required")
    @Size(min = 2, max = 100, message = "Admin name must be between 2 and 100 characters")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 4, message = "Password must be at least 4 characters")
    private String password;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid contact number format")
    private String contactNumber;

    @OneToOne
    @JoinColumn(name = "bank_id")
    @JsonBackReference
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
