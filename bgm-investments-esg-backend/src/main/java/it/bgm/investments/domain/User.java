package it.bgm.investments.domain;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entità che rappresenta un utente dell'applicazione, utilizzata per la gestione
 * delle informazioni anagrafiche e delle credenziali di accesso.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *   <li>{@link #id}</li>
 *   <li>{@link #nome}</li>
 *   <li>{@link #email}</li>
 *   <li>{@link #passwordHash}</li>
 *   <li>{@link #ruolo}</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *   <li>Metodi getter, setter e metodi standard (equals, hashCode, toString) generati da Lombok con {@code @Data}.</li>
 * </ul>
 */
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