package it.bgm.investments.mapper;

import it.bgm.investments.api.model.AssetModel;
import it.bgm.investments.api.model.AssetResponseModel;
import it.bgm.investments.config.CentralMapperConfig;
import it.bgm.investments.domain.Asset;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = CentralMapperConfig.class, uses = {Conversions.class, DateMapper.class})
public interface AssetMapper {
    @Mappings({
            @Mapping(source = "prezzo", target = "prezzo", qualifiedByName = "bdToDouble"),
            @Mapping(source = "rendimentoMensile", target = "rendimentoMensile", qualifiedByName = "bdToDouble"),
            @Mapping(source = "volatilitaMensile", target = "volatilitaMensile", qualifiedByName = "bdToDouble"),
            @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "instantToOffset")
    })
    AssetModel toModel(Asset src);

    @InheritInverseConfiguration(name = "toModel")
    @Mappings({
            @Mapping(source = "prezzo", target = "prezzo", qualifiedByName = "doubleToBd"),
            @Mapping(source = "rendimentoMensile", target = "rendimentoMensile", qualifiedByName = "doubleToBd"),
            @Mapping(source = "volatilitaMensile", target = "volatilitaMensile", qualifiedByName = "doubleToBd"),
            @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "offsetToInstant")
    })
    Asset fromModel(AssetModel dto);

    // full response = model 1:1
    default AssetResponseModel toResponse(Asset src) {
        AssetResponseModel r = new AssetResponseModel();
        AssetModel m = toModel(src);
        r.setId(m.getId());
        r.setTicker(m.getTicker());
        r.setNome(m.getNome());
        r.setSettore(m.getSettore());
        r.setPrezzo(m.getPrezzo());
        r.setEsg(m.getEsg());
        r.setRendimentoMensile(m.getRendimentoMensile());
        r.setVolatilitaMensile(m.getVolatilitaMensile());
        r.setSource(m.getSource());
        r.setUpdatedAt(m.getUpdatedAt());
        r.setActive(m.getActive());
        return r;
    }
}
