package it.bgm.investments.service;

import it.bgm.investments.api.model.CreatePortfolioBodyModel;
import it.bgm.investments.api.model.PortfolioListResponseModel;
import it.bgm.investments.api.model.PortfolioResponseModel;

public interface PortfolioService {
    PortfolioListResponseModel myPortfolios(String jSessionId);

    PortfolioResponseModel create(String jSessionId, CreatePortfolioBodyModel body);
}
