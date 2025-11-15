package it.bgm.investments.service.impl;

import it.bgm.investments.api.model.CreatePortfolioBodyModel;
import it.bgm.investments.api.model.PortfolioListResponseModel;
import it.bgm.investments.api.model.PortfolioResponseModel;
import it.bgm.investments.domain.Portfolio;
import it.bgm.investments.domain.User;
import it.bgm.investments.mapper.PortfolioMapper;
import it.bgm.investments.repo.PortfolioRepository;
import it.bgm.investments.repo.UserRepository;
import it.bgm.investments.security.AuthFacade;
import it.bgm.investments.service.PortfolioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementazione di {@link it.bgm.investments.service.PortfolioService} che
 * coordina repository, mapper e contesto di autenticazione per gestire i portafogli
 * di un utente autenticato.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>{@link #repo} — repository JPA per la persistenza dei portafogli.</li>
 *     <li>{@link #userRepo} — repository per il recupero delle informazioni sull’utente.</li>
 *     <li>{@link #mapper} — mapper per la conversione tra entità {@code Portfolio} e modelli API.</li>
 *     <li>{@link #auth} — facciata di autenticazione per risolvere l’ID utente a partire dal token di sessione.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #myPortfolios(String)} — restituisce i portafogli dell’utente
 *         identificato dal {@code jSessionId}, usando {@link #auth} e {@link #repo}.</li>
 *     <li>{@link #create(String, it.bgm.investments.api.model.CreatePortfolioBodyModel)} —
 *         crea un nuovo portafoglio per l’utente corrente, delegando la persistenza
 *         a {@link #repo} e la conversione a {@link #mapper}.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository repo;
    private final UserRepository userRepo;
    private final PortfolioMapper mapper;
    private final AuthFacade auth;

    @Override
    public PortfolioListResponseModel myPortfolios(String jSessionId) {
        Long userId = auth.userId(jSessionId);
        List<Portfolio> list = repo.findByOwnerId(userId);

        PortfolioListResponseModel res = new PortfolioListResponseModel();
        res.setItems(list.stream().map(mapper::toModel).collect(Collectors.toList()));
        res.setTotal((long) list.size());
        return res;
    }

    @Override
    public PortfolioResponseModel create(String jSessionId, CreatePortfolioBodyModel body) {
        Long userId = auth.userId(jSessionId);
        User owner = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User " + userId + " non trovato"));

        Portfolio p = new Portfolio();
        p.setNome(body.getNome());
        p.setOwner(owner);

        Portfolio saved = repo.save(p);
        return mapper.toResponse(saved);
    }
}