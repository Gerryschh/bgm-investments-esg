package it.bgm.investments.mapper;

import it.bgm.investments.api.model.SimulationResponseModel;
import it.bgm.investments.config.CentralMapperConfig;
import it.bgm.investments.domain.Simulation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

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