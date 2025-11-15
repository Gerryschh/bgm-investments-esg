package it.bgm.investments.mapper;

import it.bgm.investments.api.model.PositionModel;
import it.bgm.investments.api.model.PositionResponseModel;
import it.bgm.investments.config.CentralMapperConfig;
import it.bgm.investments.domain.PortfolioPosition;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

/**
 * Mapper MapStruct per la conversione tra entità {@link it.bgm.investments.domain.PortfolioPosition}
 * e i relativi modelli API utilizzati dal layer REST.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *   <li>Nessun campo dichiarato: l'interfaccia definisce esclusivamente metodi di mapping.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *   <li>{@link #toModel(it.bgm.investments.domain.PortfolioPosition)}: converte una entità in {@code PositionModel}.</li>
 *   <li>{@link #fromModel(it.bgm.investments.api.model.PositionModel)}: converte un {@code PositionModel} in {@code PortfolioPosition}.</li>
 *   <li>Eventuali altri metodi di mapping aggiuntivi definiti nell'interfaccia.</li>
 * </ul>
 */
@Mapper(config = CentralMapperConfig.class, uses = {PortfolioMapper.class, AssetMapper.class})
public interface PositionMapper {
    PositionModel toModel(PortfolioPosition src);

    @InheritInverseConfiguration
    PortfolioPosition fromModel(PositionModel dto);

    PositionResponseModel toResponse(PortfolioPosition src);
}