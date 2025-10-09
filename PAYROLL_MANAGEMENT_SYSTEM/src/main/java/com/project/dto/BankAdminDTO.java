package com.project.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAdminDTO {

    private Long id;
    private String name;
    private String email;
    private String contactNumber;
    private String password; // only for create/update
    private Long bankId;
}
