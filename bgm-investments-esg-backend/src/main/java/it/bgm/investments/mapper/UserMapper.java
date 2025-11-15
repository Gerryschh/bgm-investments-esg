package it.bgm.investments.mapper;

import it.bgm.investments.api.model.UserSummaryModel;
import it.bgm.investments.config.CentralMapperConfig;
import it.bgm.investments.domain.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct dedicato alla conversione tra l'entità
 * {@link it.bgm.investments.domain.User} e i modelli API
 * utilizzati per risposte complete o viste di riepilogo.
 * <p>
 * Include logiche di mapping dirette e inverse, con possibilità
 * di ignorare campi non rilevanti durante le conversioni.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>Nessun campo dichiarato: l'interfaccia è completamente stateless.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #toSummary(it.bgm.investments.domain.User)} —
 *         converte un'entità {@code User} in un {@code UserSummaryModel}.</li>
 *
 *     <li>{@link #fromSummary(it.bgm.investments.api.model.UserSummaryModel)} —
 *         converte un {@code UserSummaryModel} in un'entità {@code User},
 *         ignorando campi non mappabili secondo la configurazione.</li>
 * </ul>
 */
@Mapper(config = CentralMapperConfig.class)
public interface UserMapper {

    UserSummaryModel toSummary(User src);

    @InheritInverseConfiguration
    @Mapping(target = "passwordHash", ignore = true)
    User fromSummary(UserSummaryModel dto);
}