package it.bgm.investments.service.impl;

import it.bgm.investments.api.model.RunSimulationBodyModel;
import it.bgm.investments.api.model.SimulationResponseModel;
import it.bgm.investments.domain.Asset;
import it.bgm.investments.domain.Portfolio;
import it.bgm.investments.domain.PortfolioPosition;
import it.bgm.investments.domain.Simulation;
import it.bgm.investments.mapper.SimulationMapper;
import it.bgm.investments.repo.PortfolioPositionRepository;
import it.bgm.investments.repo.PortfolioRepository;
import it.bgm.investments.repo.SimulationRepository;
import it.bgm.investments.security.AuthFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimulationServiceImplTest {

    @Mock
    private PortfolioRepository portfolioRepo;

    @Mock
    private PortfolioPositionRepository positions;

    @Mock
    private SimulationRepository sims;

    @Mock
    private SimulationMapper mapper;

    @Mock
    private AuthFacade auth;

    @InjectMocks
    private SimulationServiceImpl service;

    @Test
    void run_executesSimulationForPortfolio() {
        Long portfolioId = 1L;
        String token = "jsess";
        Long userId = 42L;

        Portfolio p = new Portfolio();
        p.setId(portfolioId);
        var owner = new it.bgm.investments.domain.User();
        owner.setId(userId);
        p.setOwner(owner);

        RunSimulationBodyModel body = new RunSimulationBodyModel();
        body.setMesi(12);

        PortfolioPosition pos = new PortfolioPosition();

        Asset asset = new Asset();
        asset.setPrezzo(java.math.BigDecimal.valueOf(100.0));
        asset.setRendimentoMensile(java.math.BigDecimal.valueOf(0.01));
        asset.setVolatilitaMensile(java.math.BigDecimal.valueOf(0.05));
        pos.setAsset(asset);

        pos.setQuantita(java.math.BigDecimal.valueOf(10.0));

        Simulation sim = new Simulation();
        SimulationResponseModel dto = new SimulationResponseModel();

        when(auth.userId(token)).thenReturn(userId);
        when(portfolioRepo.findOwned(portfolioId, userId)).thenReturn(Optional.of(p));
        when(positions.findByPortfolioId(portfolioId)).thenReturn(List.of(pos));
        when(sims.save(any(Simulation.class))).thenReturn(sim);
        when(mapper.toResponse(sim)).thenReturn(dto);

        SimulationResponseModel res = service.run(portfolioId, body, token);

        verify(auth).userId(token);
        verify(portfolioRepo).findOwned(portfolioId, userId);
        verify(positions).findByPortfolioId(portfolioId);
        verify(sims).save(any(Simulation.class));
        verify(mapper).toResponse(sim);

        assertThat(res).isSameAs(dto);
    }
}