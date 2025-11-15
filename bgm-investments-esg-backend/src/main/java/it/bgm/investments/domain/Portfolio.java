package it.bgm.investments.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

/**
 * Entità che rappresenta un portafoglio di investimento, associato a un utente
 * e contenente una collezione di posizioni finanziarie.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *   <li>{@link #id}</li>
 *   <li>{@link #nome}</li>
 *   <li>{@link #owner}</li>
 *   <li>{@link #createdAt}</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *   <li>Metodi di accesso e metodi standard generati automaticamente da Lombok tramite {@code @Data}.</li>
 * </ul>
 */
@Entity
@Table(name = "portafogli")
@Data
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;
    private Instant createdAt = Instant.now();
}