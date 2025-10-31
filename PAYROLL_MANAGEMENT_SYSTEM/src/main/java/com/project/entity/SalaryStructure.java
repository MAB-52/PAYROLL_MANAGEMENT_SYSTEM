package com.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "salary_structures")
public class SalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 💰 Basic salary (mandatory, must be positive)
    @NotNull(message = "Basic salary is required")
    @PositiveOrZero(message = "Basic salary cannot be negative")
    @Column(nullable = false)
    private Double basic;

    // 🏠 House Rent Allowance
    @NotNull(message = "HRA is required")
    @PositiveOrZero(message = "HRA cannot be negative")
    @Column(nullable = false)
    private Double hra;

    // 💵 Dearness Allowance
    @NotNull(message = "DA is required")
    @PositiveOrZero(message = "DA cannot be negative")
    @Column(nullable = false)
    private Double da;

    // 🏦 Provident Fund
    @NotNull(message = "PF is required")
    @PositiveOrZero(message = "PF cannot be negative")
    @Column(nullable = false)
    private Double pf;

    // 🎁 Other Allowances
    @NotNull(message = "Allowances are required")
    @PositiveOrZero(message = "Allowances cannot be negative")
    @Column(nullable = false)
    private Double allowances;

    // 🧮 Net Salary (must be positive, can be auto-calculated in service)
    @NotNull(message = "Net salary is required")
    @Positive(message = "Net salary must be greater than zero")
    @Column(nullable = false)
    private Double netSalary;

    // 👷 Associated employee (mandatory)
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull(message = "Employee reference is required")
    private Employee employee;
}
