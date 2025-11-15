package it.bgm.investments.service.impl;

import it.bgm.investments.api.model.*;
import it.bgm.investments.domain.Asset;
import it.bgm.investments.mapper.AssetMapper;
import it.bgm.investments.repo.AssetRepository;
import it.bgm.investments.service.AssetService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementazione di {@link it.bgm.investments.service.AssetService} che
 * coordina repository e mapper per la gestione degli asset finanziari
 * (consultazione, creazione, aggiornamento e disattivazione).
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>{@link #repo} — repository JPA per la persistenza e la ricerca degli asset.</li>
 *     <li>{@link #mapper} — mapper per convertire tra entità {@code Asset} e modelli API.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #list(Boolean, String)} — restituisce la lista degli asset,
 *         eventualmente filtrata per attivi e settore.</li>
 *     <li>{@link #get(Long)} — recupera i dettagli di uno specifico asset tramite ID.</li>
 *     <li>{@link #create(it.bgm.investments.api.model.CreateAssetBodyModel)} —
 *         crea un nuovo asset a partire dal body fornito.</li>
 *     <li>{@link #update(Long, it.bgm.investments.api.model.UpdateAssetBodyModel)} —
 *         aggiorna i dati dell’asset indicato.</li>
 *     <li>{@link #deactivate(Long)} — imposta un asset come non attivo.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository repo;
    private final AssetMapper mapper;

    @Override
    public AssetListResponseModel list(Boolean activeOnly, String settore) {
        List<Asset> items = repo.search(activeOnly, settore);
        AssetListResponseModel res = new AssetListResponseModel();
        res.setItems(items.stream().map(mapper::toModel).collect(Collectors.toList()));
        res.setTotal((long) items.size());
        return res;
    }

    @Override
    public AssetResponseModel get(Long id) {
        Asset a = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asset " + id + " non trovato"));
        return mapper.toResponse(a);
    }

    @Override
    public AssetResponseModel create(CreateAssetBodyModel b) {
        AssetModel m = toModelFromCreate(b);
        Asset a = mapper.fromModel(m);
        Asset saved = repo.save(a);
        return mapper.toResponse(saved);
    }

    @Override
    public AssetResponseModel update(Long id, UpdateAssetBodyModel b) {
        Asset a = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asset " + id + " non trovato"));

        if (b.getNome() != null) a.setNome(b.getNome());
        if (b.getSettore() != null) a.setSettore(b.getSettore());
        if (b.getPrezzo() != null) a.setPrezzo(BigDecimal.valueOf(b.getPrezzo()));
        if (b.getEsg() != null) a.setEsg(b.getEsg());
        if (b.getRendimentoMensile() != null) a.setRendimentoMensile(BigDecimal.valueOf(b.getRendimentoMensile()));
        if (b.getVolatilitaMensile() != null) a.setVolatilitaMensile(BigDecimal.valueOf(b.getVolatilitaMensile()));
        if (b.getSource() != null) a.setSource(b.getSource());
        if (b.getActive() != null) a.setActive(b.getActive());

        Asset saved = repo.save(a);
        return mapper.toResponse(saved);
    }

    @Override
    public void deactivate(Long id) {
        Asset a = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asset " + id + " non trovato"));
        a.setActive(false);
        repo.save(a);
    }

    //HELPER
    private AssetModel toModelFromCreate(CreateAssetBodyModel b) {
        AssetModel m = new AssetModel();
        m.setTicker(b.getTicker());
        m.setNome(b.getNome());
        m.setSettore(b.getSettore());
        m.setPrezzo(b.getPrezzo());
        m.setEsg(b.getEsg());
        m.setRendimentoMensile(b.getRendimentoMensile());
        m.setVolatilitaMensile(b.getVolatilitaMensile());
        m.setSource(b.getSource());
        m.setActive(Boolean.TRUE.equals(b.getActive()));
        return m;
    }
}