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
