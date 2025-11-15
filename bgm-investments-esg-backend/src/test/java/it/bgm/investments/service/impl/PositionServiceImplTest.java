package it.bgm.investments.service.impl;

import it.bgm.investments.api.model.CreatePositionBodyModel;
import it.bgm.investments.api.model.PositionListResponseModel;
import it.bgm.investments.api.model.PositionModel;
import it.bgm.investments.api.model.PositionResponseModel;
import it.bgm.investments.domain.Asset;
import it.bgm.investments.domain.Portfolio;
import it.bgm.investments.domain.PortfolioPosition;
import it.bgm.investments.mapper.PositionMapper;
import it.bgm.investments.repo.AssetRepository;
import it.bgm.investments.repo.PortfolioPositionRepository;
import it.bgm.investments.repo.PortfolioRepository;
import it.bgm.investments.security.AuthFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceImplTest {

    @Mock
    private PortfolioRepository portfolioRepo;

    @Mock
    private PortfolioPositionRepository positionRepo;

    @Mock
    private AssetRepository assetRepo;

    @Mock
    private PositionMapper mapper;

    @Mock
    private AuthFacade auth;

    @InjectMocks
    private PositionServiceImpl service;

    @Test
    void list_returnsPositionsForPortfolio() {
        Long portfolioId = 1L;
        String token = "jsess";
        Long userId = 42L;

        Portfolio p = new Portfolio();
        p.setId(portfolioId);

        PortfolioPosition pos = new PortfolioPosition();
        PositionModel model = new PositionModel();

        when(auth.userId(token)).thenReturn(userId);
        when(portfolioRepo.findOwned(portfolioId, userId)).thenReturn(Optional.of(p));
        when(positionRepo.findByPortfolioId(portfolioId)).thenReturn(List.of(pos));
        when(mapper.toModel(pos)).thenReturn(model);

        PositionListResponseModel res = service.list(portfolioId, token);

        verify(auth).userId(token);
        verify(portfolioRepo).findOwned(portfolioId, userId);
        verify(positionRepo).findByPortfolioId(portfolioId);
        verify(mapper).toModel(pos);

        assertThat(res).isNotNull();
    }

    @Test
    void add_savesNewPosition() {
        Long portfolioId = 1L;
        String token = "jsess";
        Long userId = 42L;

        Portfolio p = new Portfolio();
        p.setId(portfolioId);

        CreatePositionBodyModel body = new CreatePositionBodyModel();
        body.setAssetId(10L);
        body.setQuantita(10.0);

        Asset asset = new Asset();
        asset.setId(10L);

        PortfolioPosition entity = new PortfolioPosition();
        PositionResponseModel resp = new PositionResponseModel();

        when(auth.userId(token)).thenReturn(userId);
        when(portfolioRepo.findOwned(portfolioId, userId)).thenReturn(Optional.of(p));
        when(assetRepo.findById(10L)).thenReturn(Optional.of(asset));
        when(positionRepo.save(any(PortfolioPosition.class))).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(resp);

        PositionResponseModel res = service.add(portfolioId, body, token);

        verify(auth).userId(token);
        verify(portfolioRepo).findOwned(portfolioId, userId);
        verify(assetRepo).findById(10L);
        verify(positionRepo).save(any(PortfolioPosition.class));
        verify(mapper).toResponse(entity);

        assertThat(res).isSameAs(resp);
    }


    @Test
    void remove_deletesPosition() {
        Long portfolioId = 1L;
        Long positionId = 10L;
        String token = "jsess";
        Long userId = 42L;

        Portfolio p = new Portfolio();
        p.setId(portfolioId);

        PortfolioPosition pos = new PortfolioPosition();
        pos.setId(positionId);
        pos.setPortfolio(p);

        when(auth.userId(token)).thenReturn(userId);
        when(portfolioRepo.findOwned(portfolioId, userId)).thenReturn(Optional.of(p));
        when(positionRepo.findByIdAndPortfolioId(positionId, portfolioId)).thenReturn(Optional.of(pos));

        service.remove(portfolioId, positionId, token);

        verify(auth).userId(token);
        verify(portfolioRepo).findOwned(portfolioId, userId);
        verify(positionRepo).findByIdAndPortfolioId(positionId, portfolioId);
        verify(positionRepo).delete(pos);
    }
}