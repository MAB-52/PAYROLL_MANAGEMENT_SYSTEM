package com.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double basic;
    private Double hra;
    private Double da;
    private Double pf;
    private Double allowances;
    private Double netSalary;
    
    @ManyToOne
    @JoinColumn(name = "employee_id") // foreign key in salary_structure table
    private Employee employee;
}
