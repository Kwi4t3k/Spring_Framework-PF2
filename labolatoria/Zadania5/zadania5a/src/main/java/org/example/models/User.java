package org.example.models;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(nullable = false, unique = true)
    private String id;
    @Column(nullable = false, unique = true)
    private String login;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role;
}