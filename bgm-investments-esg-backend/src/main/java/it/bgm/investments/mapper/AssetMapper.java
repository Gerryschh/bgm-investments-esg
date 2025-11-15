package it.bgm.investments.mapper;

import it.bgm.investments.api.model.AssetModel;
import it.bgm.investments.api.model.AssetResponseModel;
import it.bgm.investments.config.CentralMapperConfig;
import it.bgm.investments.domain.Asset;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper MapStruct dedicato alla conversione tra l'entità {@link it.bgm.investments.domain.Asset}
 * e i corrispondenti modelli API, con supporto a conversioni numeriche e temporali tramite
 * componenti ausiliari come {@link Conversions} e {@link DateMapper}.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *   <li>Nessun campo dichiarato: l'interfaccia definisce solo metodi di mapping.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *   <li>{@link #toModel(it.bgm.investments.domain.Asset)} – converte un'entità {@code Asset} in {@code AssetModel}.</li>
 *   <li>{@link #fromModel(it.bgm.investments.api.model.AssetModel)} – converte un {@code AssetModel} in entità {@code Asset} (metodo inverso).</li>
 *   <li>{@link #toResponse(it.bgm.investments.domain.Asset)} – converte un'entità {@code Asset} in un {@code AssetResponseModel} utilizzando una mappatura personalizzata.</li>
 * </ul>
 *
 * <p>Il mapper applica conversioni specifiche (come BigDecimal→Double e Instant→OffsetDateTime)
 * tramite annotazioni {@code @Mappings} e qualificatori {@code @Named} presenti nelle classi di supporto.</p>
 */
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