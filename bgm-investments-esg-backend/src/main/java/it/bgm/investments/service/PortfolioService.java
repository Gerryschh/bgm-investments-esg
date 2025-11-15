package it.bgm.investments.service;

import it.bgm.investments.api.model.CreatePortfolioBodyModel;
import it.bgm.investments.api.model.PortfolioListResponseModel;
import it.bgm.investments.api.model.PortfolioResponseModel;

/**
 * Servizio applicativo per la gestione dei portafogli lato utente.
 * Espone le operazioni per ottenere l’elenco dei portafogli dell’utente
 * autenticato e per crearne di nuovi.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>Nessun campo dichiarato: l'interfaccia definisce solo il contratto del servizio.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #myPortfolios(String)} — restituisce l'elenco dei portafogli
 *         associati alla sessione identificata dal valore {@code jSessionId}.</li>
 *     <li>{@link #create(String, it.bgm.investments.api.model.CreatePortfolioBodyModel)} —
 *         crea un nuovo portafoglio per l’utente identificato dal {@code jSessionId}
 *         utilizzando i dati specificati nel body.</li>
 * </ul>
 */
public interface PortfolioService {
    PortfolioListResponseModel myPortfolios(String jSessionId);

    PortfolioResponseModel create(String jSessionId, CreatePortfolioBodyModel body);
}
