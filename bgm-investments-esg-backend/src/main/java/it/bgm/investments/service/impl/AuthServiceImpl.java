package it.bgm.investments.service.impl;

import it.bgm.investments.api.model.CurrentUserResponseModel;
import it.bgm.investments.api.model.LoginBodyModel;
import it.bgm.investments.api.model.LoginResponseModel;
import it.bgm.investments.domain.User;
import it.bgm.investments.mapper.UserMapper;
import it.bgm.investments.repo.UserRepository;
import it.bgm.investments.security.SessionStore;
import it.bgm.investments.security.UserPrincipal;
import it.bgm.investments.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementazione di {@link it.bgm.investments.service.AuthService} che
 * gestisce il flusso di autenticazione basato su credenziali, la creazione
 * delle sessioni applicative e la risoluzione dell’utente corrente.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>{@link #userRepo} — repository per il recupero dell’utente a partire dalle credenziali.</li>
 *     <li>{@link #sessions} — store delle sessioni usato per creare, validare e invalidare i token.</li>
 *     <li>{@link #userMapper} — mapper per convertire l’entità utente nei modelli di risposta.</li>
 *     <li>{@link #passwordEncoder} — encoder delle password utilizzato per la verifica delle credenziali.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #login(it.bgm.investments.api.model.LoginBodyModel)} —
 *         autentica l’utente, verifica la password e crea una nuova sessione tramite {@link #sessions}.</li>
 *     <li>{@link #currentUser(String)} — restituisce le informazioni dell’utente
 *         associato al {@code jSessionId} indicato, utilizzando {@link #sessions}
 *         e {@link #userMapper}.</li>
 *     <li>{@link #logout(String)} — invalida la sessione corrispondente al token
 *         passato, delegando a {@link #sessions}.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final SessionStore sessions;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseModel login(LoginBodyModel body) throws AuthException {
        // 1. Recupera l'utente dal DB
        User user = userRepo.findByEmail(body.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Utente con email " + body.getEmail() + " non trovato"));

        // 2. Verifica la password hashata
        boolean passwordOk = passwordEncoder.matches(body.getPassword(), user.getPasswordHash());
        if (!passwordOk) {
            throw new AuthException("Password non corretta");
        }

        // 3. Crea una sessione e restituisce il token
        String token = sessions.create(user.getId());
        LoginResponseModel response = new LoginResponseModel();
        response.setUser(userMapper.toSummary(user));
        response.setSessionId(token);
        return response;
    }

    @Override
    public CurrentUserResponseModel currentUser(String jSessionId) {
        Long uid;
        if (jSessionId != null && !jSessionId.isBlank()) {
            uid = sessions.userId(jSessionId);
        } else {
            var auth = SecurityContextHolder
                    .getContext().getAuthentication();
            if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal up)) {
                throw new SecurityException("JSESSIONID mancante");
            }
            uid = up.getUserId();
        }
        User u = userRepo.findById(uid)
                .orElseThrow(() -> new EntityNotFoundException("User " + uid + " non trovato"));

        CurrentUserResponseModel cur = new CurrentUserResponseModel();
        cur.setId(u.getId());
        cur.setNome(u.getNome());
        cur.setEmail(u.getEmail());
        cur.setRuolo(CurrentUserResponseModel.RuoloEnum.valueOf(u.getRuolo()));
        return cur;
    }

    @Override
    public void logout(String jSessionId) {
        sessions.invalidate(jSessionId);
    }
}