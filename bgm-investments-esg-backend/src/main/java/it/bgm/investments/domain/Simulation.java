package it.bgm.investments.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

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