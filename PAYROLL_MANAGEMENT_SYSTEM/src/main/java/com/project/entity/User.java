package com.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    private String role; // e.g. "ADMIN", "EMPLOYEE", etc.

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;
}
