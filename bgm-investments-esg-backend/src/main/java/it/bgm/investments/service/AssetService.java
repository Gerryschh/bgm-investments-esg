package it.bgm.investments.service;

import it.bgm.investments.api.model.AssetListResponseModel;
import it.bgm.investments.api.model.AssetResponseModel;
import it.bgm.investments.api.model.CreateAssetBodyModel;
import it.bgm.investments.api.model.UpdateAssetBodyModel;

/**
 * Servizio per la gestione degli asset finanziari presenti nel sistema.
 * Fornisce operazioni di consultazione, creazione, aggiornamento e disattivazione
 * degli strumenti gestiti.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>Nessun campo dichiarato: l'interfaccia definisce le operazioni disponibili sugli asset.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #list(Boolean, String, String)} — restituisce la lista degli asset,
 *         eventualmente filtrata per stato attivo e/o settore.</li>
 *     <li>{@link #get(Long, String)} — recupera i dettagli di un singolo asset tramite ID.</li>
 *     <li>{@link #create(it.bgm.investments.api.model.CreateAssetBodyModel, String)} —
 *         crea un nuovo asset a partire dai dati forniti nel body.</li>
 *     <li>{@link #update(Long, it.bgm.investments.api.model.UpdateAssetBodyModel, String)} —
 *         aggiorna l’asset esistente identificato dall’ID.</li>
 *     <li>{@link #deactivate(Long, String)} — disattiva l’asset indicato, rendendolo non più utilizzabile.</li>
 * </ul>
 */
public interface AssetService {
    AssetListResponseModel list(Boolean activeOnly, String settore, String jSessionId);

    AssetResponseModel get(Long id, String jSessionId);

    AssetResponseModel create(CreateAssetBodyModel b, String jSessionId);

    AssetResponseModel update(Long id, UpdateAssetBodyModel b, String jSessionId);

    void deactivate(Long id, String jSessionId);
}
