package it.bgm.investments.mapper;

import it.bgm.investments.api.model.PortfolioModel;
import it.bgm.investments.api.model.PortfolioResponseModel;
import it.bgm.investments.config.CentralMapperConfig;
import it.bgm.investments.domain.Portfolio;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.ZoneOffset;

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