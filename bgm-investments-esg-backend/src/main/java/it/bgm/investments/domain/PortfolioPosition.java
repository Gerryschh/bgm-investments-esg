package it.bgm.investments.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "posizioni")
@Data
public class PortfolioPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Portfolio portfolio;
    @ManyToOne(fetch = FetchType.LAZY)
    private Asset asset;
    private BigDecimal quantita; // numero quote/azioni
}