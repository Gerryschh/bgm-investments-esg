package it.bgm.investments.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Rappresenta una simulazione associata a un portafoglio, utilizzata per
 * memorizzare parametri e risultati di scenari previsionali.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *   <li>{@link #id}</li>
 *   <li>{@link #portfolio}</li>
 *   <li>{@link #mesi}</li>
 *   <li>{@link #esg}</li>
 *   <li>{@link #rendimentoAtteso}</li>
 *   <li>{@link #volatilita}</li>
 *   <li>{@link #scenarioNeutro}</li>
 *   <li>{@link #scenarioOttimistico}</li>
 *   <li>{@link #scenarioPessimistico}</li>
 *   <li>{@link #createdAt}</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *   <li>Metodi di accesso, modifica e utilità generati automaticamente da Lombok tramite {@code @Data}.</li>
 * </ul>
 */
@Entity
@Table(name = "simulazioni")
@Data
public class Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Portfolio portfolio;
    private Integer mesi;
    private BigDecimal esg;
    private BigDecimal rendimentoAtteso;
    private BigDecimal volatilita;
    private BigDecimal scenarioNeutro;
    private BigDecimal scenarioOttimistico;
    private BigDecimal scenarioPessimistico;
    private Instant createdAt = Instant.now();
}