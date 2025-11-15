package it.bgm.investments.web;

import it.bgm.investments.api.AdminApiDelegate;
import it.bgm.investments.api.model.AssetResponseModel;
import it.bgm.investments.api.model.CreateAssetBodyModel;
import it.bgm.investments.api.model.UpdateAssetBodyModel;
import it.bgm.investments.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Implementazione di {@link it.bgm.investments.api.AdminApiDelegate} che espone
 * le operazioni amministrative sugli asset tramite API REST. Coordina la logica
 * di business fornita da {@link AssetService} e costruisce le risposte HTTP
 * conformi alle specifiche dell'API.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>{@link #assetService} — servizio applicativo responsabile della
 *         gestione degli asset (creazione, aggiornamento, disattivazione).</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #createAsset(it.bgm.investments.api.model.CreateAssetBodyModel)} —
 *         crea un nuovo asset tramite il {@link AssetService} e restituisce
 *         una risposta HTTP <code>201 Created</code> contenente il modello risultante.</li>
 *
 *     <li>{@link #updateAsset(it.bgm.investments.api.model.UpdateAssetBodyModel, Long)} —
 *         aggiorna un asset esistente identificato da <code>assetId</code> e restituisce
 *         una risposta <code>200 OK</code> con il modello aggiornato.</li>
 *
 *     <li>{@link #deactivateAsset(Long)} —
 *         disattiva l’asset indicato e restituisce una risposta <code>204 No Content</code>
 *         al termine dell’operazione.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AdminApiDelegateImpl extends BaseApiDelegate implements AdminApiDelegate {

    private final AssetService assetService;

    @Override
    public ResponseEntity<AssetResponseModel> createAsset(@Valid CreateAssetBodyModel body) {
        return ResponseEntity.status(201).body(assetService.create(body));
    }

    @Override
    public ResponseEntity<AssetResponseModel> updateAsset(@Valid UpdateAssetBodyModel body, Long assetId) {
        return ResponseEntity.ok(assetService.update(assetId, body));
    }

    @Override
    public ResponseEntity<Void> deactivateAsset(Long assetId) {
        assetService.deactivate(assetId);
        return ResponseEntity.noContent().build();
    }
}