package it.bgm.investments.service;

import it.bgm.investments.api.model.RunSimulationBodyModel;
import it.bgm.investments.api.model.SimulationResponseModel;

/**
 * Servizio che si occupa di eseguire simulazioni finanziarie su un portafoglio,
 * producendo un risultato strutturato a partire dai parametri di input richiesti.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>Nessun campo dichiarato: l'interfaccia espone solo l’operazione di simulazione.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #run(Long, it.bgm.investments.api.model.RunSimulationBodyModel, String)} —
 *         esegue una simulazione per il portafoglio indicato, utilizzando i parametri
 *         forniti e verificando i diritti legati al {@code jSessionId}.</li>
 * </ul>
 */
public interface SimulationService {
    SimulationResponseModel run(Long portfolioId, RunSimulationBodyModel body, String jSessionId);
}
