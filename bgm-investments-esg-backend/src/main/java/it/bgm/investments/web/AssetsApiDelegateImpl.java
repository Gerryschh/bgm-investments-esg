package it.bgm.investments.web;

import it.bgm.investments.api.AssetsApiDelegate;
import it.bgm.investments.api.model.AssetListResponseModel;
import it.bgm.investments.api.model.AssetResponseModel;
import it.bgm.investments.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Implementazione di {@link it.bgm.investments.api.AssetsApiDelegate} che
 * espone le operazioni di consultazione degli asset tramite API REST.
 * Delegando la logica al {@link AssetService}, fornisce endpoints per
 * il recupero della lista degli asset e il dettaglio di un singolo asset.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>{@link #assetService} — servizio applicativo responsabile della
 *         consultazione e gestione degli asset.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #getAssets(Boolean, String)} —
 *         restituisce la lista degli asset filtrata opzionalmente per
 *         stato attivo e settore, delegando a {@link AssetService#list(Boolean, String)}.</li>
 *
 *     <li>{@link #getAssetById(Long)} —
 *         recupera il dettaglio di un asset tramite il suo ID,
 *         delegando a {@link AssetService#get(Long)}.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AssetsApiDelegateImpl extends BaseApiDelegate implements AssetsApiDelegate {

    private final AssetService assetService;

    @Override
    public ResponseEntity<AssetListResponseModel> getAssets(Boolean activeOnly, String settore) {
        return ResponseEntity.ok(assetService.list(activeOnly, settore));
    }

    @Override
    public ResponseEntity<AssetResponseModel> getAssetById(Long assetId) {
        return ResponseEntity.ok(assetService.get(assetId));
    }
}