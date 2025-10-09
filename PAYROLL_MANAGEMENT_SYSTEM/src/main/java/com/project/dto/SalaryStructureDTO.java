package com.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryStructureDTO {
	private Long id;
    private Double basic;
    private Double hra;
    private Double da;
    private Double pf;
    private Double allowances;
    private Double netSalary;
}
