package it.bgm.investments.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Entità che rappresenta una singola posizione all'interno di un portafoglio,
 * collegata a un asset e alla relativa quantità detenuta.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *   <li>{@link #id}</li>
 *   <li>{@link #portfolio}</li>
 *   <li>{@link #asset}</li>
 *   <li>{@link #quantita}</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *   <li>Metodi di accesso e modifica della posizione generati da Lombok tramite {@code @Data}.</li>
 * </ul>
 */
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