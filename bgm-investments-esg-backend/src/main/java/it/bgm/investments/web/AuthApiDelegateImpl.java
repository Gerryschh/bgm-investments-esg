package it.bgm.investments.web;

import it.bgm.investments.api.AutenticazioneApiDelegate;
import it.bgm.investments.api.model.CurrentUserResponseModel;
import it.bgm.investments.api.model.LoginBodyModel;
import it.bgm.investments.api.model.LoginResponseModel;
import it.bgm.investments.service.AuthService;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Implementazione di {@link it.bgm.investments.api.AutenticazioneApiDelegate}
 * che gestisce le operazioni di autenticazione e recupero delle informazioni
 * sull’utente corrente tramite API REST. Si appoggia a {@link AuthService}
 * per la logica di login, logout e risoluzione dell’utente associato alla
 * sessione corrente.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>{@link #authService} — servizio applicativo che gestisce login,
 *         logout e recupero dell’utente corrente.</li>
 *     <li>{@link #request} — richiesta HTTP corrente, utilizzata per estrarre
 *         il token di sessione tramite {@link BaseApiDelegate#getJSessionId(jakarta.servlet.http.HttpServletRequest)}.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #getCurrentUser()} —
 *         restituisce le informazioni dell’utente autenticato,
 *         recuperando il token di sessione dalla richiesta e delegando
 *         a {@link AuthService#currentUser(String)}.</li>
 *
 *     <li>{@link #login(it.bgm.investments.api.model.LoginBodyModel)} —
 *         effettua l’autenticazione sulla base delle credenziali fornite.
 *         In caso di errore dal servizio, converte l’{@link AuthException}
 *         in una {@link RuntimeException}.</li>
 *
 *     <li>{@link #logout()} —
 *         invalida la sessione corrente identificata tramite header,
 *         delegando a {@link AuthService#logout(String)} e restituendo
 *         una risposta <code>204 No Content</code>.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AuthApiDelegateImpl extends BaseApiDelegate implements AutenticazioneApiDelegate {

    private final AuthService authService;
    private final HttpServletRequest request;

    @Override
    public ResponseEntity<CurrentUserResponseModel> getCurrentUser() {
        return ResponseEntity.ok(authService.currentUser(getJSessionId(request)));
    }

    @Override
    public ResponseEntity<LoginResponseModel> login(@Valid LoginBodyModel body) {
        try {
            return ResponseEntity.ok(authService.login(body));
        } catch (AuthException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<Void> logout() {
        authService.logout(getJSessionId(request));
        return ResponseEntity.noContent().build();
    }
}