package it.bgm.investments.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Entità che rappresenta uno strumento finanziario (titolo) memorizzato nel sistema,
 * comprensivo di informazioni identificative, dati quantitativi e stato di attivazione.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *   <li>{@link #id}</li>
 *   <li>{@link #ticker}</li>
 *   <li>{@link #nome}</li>
 *   <li>{@link #settore}</li>
 *   <li>{@link #prezzo}</li>
 *   <li>{@link #esg}</li>
 *   <li>{@link #rendimentoMensile}</li>
 *   <li>{@link #volatilitaMensile}</li>
 *   <li>{@link #source}</li>
 *   <li>{@link #updatedAt}</li>
 *   <li>{@link #active}</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *   <li>Metodi di accesso e gestione dei dati dell'asset generati automaticamente da Lombok tramite {@code @Data}.</li>
 * </ul>
 */
@Entity
@Table(name = "titoli")
@Data
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String ticker;
    private String nome;
    private String settore;
    private BigDecimal prezzo; // base price demo
    private Integer esg; // 0..100
    private BigDecimal rendimentoMensile; // r
    private BigDecimal volatilitaMensile; // sigma
    private String source; // MANUAL/API
    private Instant updatedAt;
    private Boolean active = true;
}