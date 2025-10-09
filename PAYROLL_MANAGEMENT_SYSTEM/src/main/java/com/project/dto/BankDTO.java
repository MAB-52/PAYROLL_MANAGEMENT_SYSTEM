package com.project.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankDTO {

    private Long id;
    private String bankName;
    private String branchCode;
    private List<Long> organizationIds; // organizations linked to this bank
}
