package it.bgm.investments.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

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