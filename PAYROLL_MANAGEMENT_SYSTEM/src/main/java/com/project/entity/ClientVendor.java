package com.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientVendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cvId;

    @ManyToOne
    @JoinColumn(name = "org_id")
    private Organization organization;

    private String name;

    @Enumerated(EnumType.STRING)
    private ClientVendorType type;

    private String email;
    private String accountNumber;
    private String ifscCode;
    private String gstNumber;
}
