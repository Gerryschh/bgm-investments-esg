package it.bgm.investments.mapper;

import it.bgm.investments.api.model.SimulationResponseModel;
import it.bgm.investments.config.CentralMapperConfig;
import it.bgm.investments.domain.Simulation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper MapStruct dedicato alla conversione dell'entità
 * {@link it.bgm.investments.domain.Simulation} nel modello API
 * {@link it.bgm.investments.api.model.SimulationResponseModel}.
 * <p>
 * Integra conversioni numeriche e temporali tramite i componenti
 * {@link Conversions}, {@link DateMapper} e la gestione del portafoglio
 * tramite {@link PortfolioMapper}.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *   <li>Nessun campo dichiarato: l'interfaccia è stateless e definisce solo metodi di mapping.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *   <li>{@link #toResponse(Simulation)} — converte una entità {@code Simulation}
 *       in un {@code SimulationResponseModel}, applicando le conversioni
 *       configurate tramite {@code @Mappings}.</li>
 * </ul>
 */
@Mapper(config = CentralMapperConfig.class, uses = {PortfolioMapper.class, Conversions.class, DateMapper.class})
public interface SimulationMapper {
    @Mappings({
            @Mapping(source = "esg", target = "esg", qualifiedByName = "bdToDouble"),
            @Mapping(source = "rendimentoAtteso", target = "rendimentoAtteso", qualifiedByName = "bdToDouble"),
            @Mapping(source = "volatilita", target = "volatilita", qualifiedByName = "bdToDouble"),
            @Mapping(source = "scenarioNeutro", target = "scenarioNeutro", qualifiedByName = "bdToDouble"),
            @Mapping(source = "scenarioOttimistico", target = "scenarioOttimistico", qualifiedByName = "bdToDouble"),
            @Mapping(source = "scenarioPessimistico", target = "scenarioPessimistico", qualifiedByName = "bdToDouble"),
            @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "instantToOffset")
    })
    SimulationResponseModel toResponse(Simulation s);
}