package it.bgm.investments.service.impl;

import it.bgm.investments.api.model.CreatePositionBodyModel;
import it.bgm.investments.api.model.PositionListResponseModel;
import it.bgm.investments.api.model.PositionResponseModel;
import it.bgm.investments.domain.Asset;
import it.bgm.investments.domain.Portfolio;
import it.bgm.investments.domain.PortfolioPosition;
import it.bgm.investments.mapper.PositionMapper;
import it.bgm.investments.repo.AssetRepository;
import it.bgm.investments.repo.PortfolioPositionRepository;
import it.bgm.investments.repo.PortfolioRepository;
import it.bgm.investments.security.AuthFacade;
import it.bgm.investments.service.PositionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PortfolioRepository portfolioRepo;
    private final PortfolioPositionRepository positionRepo;
    private final AssetRepository assetRepo;
    private final PositionMapper mapper;
    private final AuthFacade auth;

    @Override
    public PositionListResponseModel list(Long portfolioId, String jSessionId) {
        Long userId = auth.userId(jSessionId);
        Portfolio p = portfolioRepo.findOwned(portfolioId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio " + portfolioId + " non trovato o non posseduto"));

        List<PortfolioPosition> positions = positionRepo.findByPortfolioId(p.getId());

        PositionListResponseModel res = new PositionListResponseModel();
        res.setItems(positions.stream().map(mapper::toModel).collect(Collectors.toList()));
        res.setTotal((long) positions.size());
        return res;
    }

    @Override
    public PositionResponseModel add(Long portfolioId, CreatePositionBodyModel body, String jSessionId) {
        Long userId = auth.userId(jSessionId);
        Portfolio p = portfolioRepo.findOwned(portfolioId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio " + portfolioId + " non trovato o non posseduto"));

        Asset a = assetRepo.findById(body.getAssetId())
                .orElseThrow(() -> new EntityNotFoundException("Asset " + body.getAssetId() + " non trovato"));

        PortfolioPosition pos = new PortfolioPosition();
        pos.setPortfolio(p);
        pos.setAsset(a);
        pos.setQuantita(BigDecimal.valueOf(body.getQuantita()));

        PortfolioPosition saved = positionRepo.save(pos);
        return mapper.toResponse(saved);
    }

    @Override
    public void remove(Long portfolioId, Long positionId, String jSessionId) {
        Long userId = auth.userId(jSessionId);
        Portfolio p = portfolioRepo.findOwned(portfolioId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio " + portfolioId + " non trovato o non posseduto"));

        PortfolioPosition pos = positionRepo.findByIdAndPortfolioId(positionId, p.getId())
                .orElseThrow(() -> new EntityNotFoundException("Position " + positionId + " non trovata nel portafoglio " + portfolioId));

        positionRepo.delete(pos);
    }
}