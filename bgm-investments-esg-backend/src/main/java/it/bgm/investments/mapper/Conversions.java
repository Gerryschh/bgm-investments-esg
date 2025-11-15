package it.bgm.investments.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Componente di utilità che fornisce metodi di conversione tra
 * {@link java.math.BigDecimal} e {@link java.lang.Double}, utilizzati dai mapper
 * per normalizzare i tipi numerici durante le trasformazioni dei dati.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *   <li>Nessun campo dichiarato: la classe è stateless e contiene solo metodi di utilità.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *   <li>{@link #bdToDouble(BigDecimal)} – converte un {@code BigDecimal} in {@code Double},
 *       restituendo {@code null} se il valore di input è nullo.</li>
 *   <li>{@link #doubleToBd(Double)} – converte un {@code Double} in {@code BigDecimal},
 *       restituendo {@code null} se il valore di input è nullo.</li>
 * </ul>
 */
@Component
public class Conversions {
    @Named("bdToDouble")
    public Double bdToDouble(BigDecimal v) {
        return v == null ? null : v.doubleValue();
    }

    @Named("doubleToBd")
    public BigDecimal doubleToBd(Double v) {
        return v == null ? null : BigDecimal.valueOf(v);
    }
}