package com.event.hab.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String passwordHash;

    @Column (nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column (updatable = false)
    private LocalDateTime createdAt;


}
