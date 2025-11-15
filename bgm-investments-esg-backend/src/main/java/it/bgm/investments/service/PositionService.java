package it.bgm.investments.service;

import it.bgm.investments.api.model.CreatePositionBodyModel;
import it.bgm.investments.api.model.PositionListResponseModel;
import it.bgm.investments.api.model.PositionResponseModel;

/**
 * Servizio applicativo per la gestione delle posizioni all'interno di un portafoglio.
 * Fornisce operazioni per elencare, aggiungere e rimuovere posizioni, verificando
 * i permessi dell’utente autenticato.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>Nessun campo dichiarato: l'interfaccia definisce solo le operazioni esposte.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #list(Long, String)} — restituisce la lista delle posizioni
 *         per il portafoglio indicato, validando la sessione tramite {@code jSessionId}.</li>
 *     <li>{@link #add(Long, it.bgm.investments.api.model.CreatePositionBodyModel, String)} —
 *         aggiunge una nuova posizione al portafoglio indicato per l’utente
 *         associato alla sessione.</li>
 *     <li>{@link #remove(Long, Long, String)} — rimuove una posizione dal portafoglio,
 *         verificando che appartenga al portafoglio dell’utente corrente.</li>
 * </ul>
 */
public interface PositionService {
    PositionListResponseModel list(Long portfolioId, String jSessionId);

    PositionResponseModel add(Long portfolioId, CreatePositionBodyModel body, String jSessionId);

    void remove(Long portfolioId, Long positionId, String jSessionId);
}
