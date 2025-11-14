package it.bgm.investments.mapper;

import it.bgm.investments.api.model.PositionModel;
import it.bgm.investments.api.model.PositionResponseModel;
import it.bgm.investments.config.CentralMapperConfig;
import it.bgm.investments.domain.PortfolioPosition;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class, uses = {PortfolioMapper.class, AssetMapper.class})
public interface PositionMapper {
    PositionModel toModel(PortfolioPosition src);

    @InheritInverseConfiguration
    PortfolioPosition fromModel(PositionModel dto);

    PositionResponseModel toResponse(PortfolioPosition src);
}