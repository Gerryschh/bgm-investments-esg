package it.bgm.investments.service;

import it.bgm.investments.api.model.RunSimulationBodyModel;
import it.bgm.investments.api.model.SimulationResponseModel;

public interface SimulationService {
    SimulationResponseModel run(Long portfolioId, RunSimulationBodyModel body, String jSessionId);
}
