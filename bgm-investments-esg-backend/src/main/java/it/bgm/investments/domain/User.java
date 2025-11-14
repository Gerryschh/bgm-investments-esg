package it.bgm.investments.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "utenti")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    @Column(name = "password_hash")
    private String passwordHash;
    private String ruolo; // "USER" / "ADMIN"
}