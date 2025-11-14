package it.bgm.investments.service;

import it.bgm.investments.api.model.CreatePositionBodyModel;
import it.bgm.investments.api.model.PositionListResponseModel;
import it.bgm.investments.api.model.PositionResponseModel;

public interface PositionService {
    PositionListResponseModel list(Long portfolioId, String jSessionId);

    PositionResponseModel add(Long portfolioId, CreatePositionBodyModel body, String jSessionId);

    void remove(Long portfolioId, Long positionId, String jSessionId);
}
