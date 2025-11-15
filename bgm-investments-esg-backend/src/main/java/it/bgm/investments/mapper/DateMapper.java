package it.bgm.investments.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;

/**
 * Componente di supporto utilizzato dai mapper per convertire valori temporali
 * tra {@link java.time.Instant} e {@link java.time.OffsetDateTime}, con gestione
 * trasparente dei valori null e normalizzazione verso il fuso orario UTC.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *   <li>Nessun campo dichiarato: la classe è stateless e fornisce esclusivamente metodi di conversione.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *   <li>{@link #offsetToInstant(OffsetDateTime)} — converte un {@code OffsetDateTime} in {@code Instant}.</li>
 *   <li>{@link #instantToOffset(Instant)} — converte un {@code Instant} in {@code OffsetDateTime} UTC.</li>
 * </ul>
 */
@Component
public class DateMapper {

    @Named("offsetToInstant")
    public Instant offsetToInstant(OffsetDateTime odt) {
        return odt != null ? odt.toInstant() : null;
    }

    @Named("instantToOffset")
    public OffsetDateTime instantToOffset(Instant instant) {
        return instant != null ? OffsetDateTime.ofInstant(instant, java.time.ZoneOffset.UTC) : null;
    }
}