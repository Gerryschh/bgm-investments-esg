package it.bgm.investments.mapper;

import it.bgm.investments.api.model.PortfolioModel;
import it.bgm.investments.api.model.PortfolioResponseModel;
import it.bgm.investments.config.CentralMapperConfig;
import it.bgm.investments.domain.Portfolio;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.ZoneOffset;

/**
 * Mapper MapStruct responsabile della conversione tra l'entità
 * {@link it.bgm.investments.domain.Portfolio} e i corrispondenti
 * modelli API, includendo il supporto alle conversioni temporali e
 * alla gestione del proprietario tramite {@link UserMapper}.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *   <li>Nessun campo dichiarato: l'interfaccia è priva di stato e definisce solo metodi di mapping.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *   <li>{@link #toModel(Portfolio)} —
 *       converte un'entità {@code Portfolio} in {@code PortfolioModel},
 *       applicando la conversione del campo {@code createdAt} tramite {@code instantToOffset}.</li>
 *
 *   <li>{@link #fromModel(PortfolioModel)} —
 *       converte un {@code PortfolioModel} nell'entità {@code Portfolio},
 *       invertendo automaticamente la mappatura e convertendo {@code createdAt}
 *       tramite {@code offsetToInstant}.</li>
 *
 *   <li>{@link #toResponse(Portfolio)} —
 *       costruisce un {@code PortfolioResponseModel} attraverso logica custom,
 *       convertendo manualmente i campi e normalizzando il timestamp in UTC.</li>
 * </ul>
 *
 * <p>Il mapper sfrutta {@link DateMapper} per la gestione dei campi temporali
 * e {@link UserMapper} per convertire il proprietario del portafoglio.</p>
 */
@Mapper(config = CentralMapperConfig.class, uses = {UserMapper.class, DateMapper.class})
public interface PortfolioMapper {
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "instantToOffset")
    PortfolioModel toModel(Portfolio src);

    @InheritInverseConfiguration
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "offsetToInstant")
    Portfolio fromModel(PortfolioModel dto);

    default PortfolioResponseModel toResponse(Portfolio src) {
        PortfolioResponseModel r = new PortfolioResponseModel();
        r.setId(src.getId());
        r.setNome(src.getNome());
        r.setOwner(src.getOwner() == null ? null : toModel(src).getOwner());
        r.setCreatedAt(src.getCreatedAt().atOffset(ZoneOffset.UTC));
        return r;
    }
}