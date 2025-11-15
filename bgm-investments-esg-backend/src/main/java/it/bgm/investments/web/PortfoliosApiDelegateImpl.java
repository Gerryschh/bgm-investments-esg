package it.bgm.investments.web;

import it.bgm.investments.api.PortfoliosApiDelegate;
import it.bgm.investments.api.model.*;
import it.bgm.investments.service.PortfolioService;
import it.bgm.investments.service.PositionService;
import it.bgm.investments.service.SimulationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Implementazione di {@link it.bgm.investments.api.PortfoliosApiDelegate} che
 * espone tramite API REST tutte le operazioni relative ai portafogli: gestione
 * dei portafogli dell’utente, posizioni al loro interno e simulazioni finanziarie.
 * Coordina i servizi di dominio ({@link PortfolioService}, {@link PositionService},
 * {@link SimulationService}) e utilizza {@link BaseApiDelegate} per risolvere
 * il token di sessione dalla richiesta HTTP.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>{@link #request} — richiesta HTTP corrente, utilizzata per ricavare il token di sessione.</li>
 *     <li>{@link #portfolioService} — servizio per la gestione dei portafogli dell’utente.</li>
 *     <li>{@link #positionService} — servizio per la gestione delle posizioni nei portafogli.</li>
 *     <li>{@link #simulationService} — servizio che esegue simulazioni sui portafogli.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #getMyPortfolios()} —
 *         restituisce la lista dei portafogli dell’utente autenticato.</li>
 *
 *     <li>{@link #createPortfolio(it.bgm.investments.api.model.CreatePortfolioBodyModel)} —
 *         crea un nuovo portafoglio per l’utente associato al token corrente.</li>
 *
 *     <li>{@link #getPortfolioPositions(Long)} —
 *         restituisce l’elenco delle posizioni presenti nel portafoglio indicato,
 *         verificandone l’appartenenza all’utente.</li>
 *
 *     <li>{@link #addPosition(it.bgm.investments.api.model.CreatePositionBodyModel, Long)} —
 *         aggiunge una nuova posizione al portafoglio indicato.</li>
 *
 *     <li>{@link #removePosition(Long, Long)} —
 *         rimuove una posizione dal portafoglio dopo la verifica dei permessi.</li>
 *
 *     <li>{@link #runSimulation(it.bgm.investments.api.model.RunSimulationBodyModel, Long)} —
 *         esegue una simulazione sul portafoglio indicato utilizzando i parametri forniti.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class PortfoliosApiDelegateImpl extends BaseApiDelegate implements PortfoliosApiDelegate  {

    private final HttpServletRequest request;
    private final PortfolioService portfolioService;
    private final PositionService positionService;
    private final SimulationService simulationService;

    @Override
    public ResponseEntity<PortfolioListResponseModel> getMyPortfolios() {
        return ResponseEntity.ok(portfolioService.myPortfolios(getJSessionId(request)));
    }

    @Override
    public ResponseEntity<PortfolioResponseModel> createPortfolio(@Valid CreatePortfolioBodyModel body) {
        return ResponseEntity.status(201).body(portfolioService.create(getJSessionId(request), body));
    }

    @Override
    public ResponseEntity<PositionListResponseModel> getPortfolioPositions(Long portfolioId) {
        return ResponseEntity.ok(positionService.list(portfolioId, getJSessionId(request)));
    }

    @Override
    public ResponseEntity<PositionResponseModel> addPosition(@Valid CreatePositionBodyModel body, Long portfolioId) {
        return ResponseEntity.status(201).body(positionService.add(portfolioId, body, getJSessionId(request)));
    }

    @Override
    public ResponseEntity<Void> removePosition(Long portfolioId, Long positionId) {
        positionService.remove(portfolioId, positionId, getJSessionId(request));
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<SimulationResponseModel> runSimulation(@Valid RunSimulationBodyModel body, Long portfolioId) {
        return ResponseEntity.ok(simulationService.run(portfolioId, body, getJSessionId(request)));
    }
}
