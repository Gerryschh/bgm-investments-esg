package it.bgm.investments.service;

import it.bgm.investments.api.model.CurrentUserResponseModel;
import it.bgm.investments.api.model.LoginBodyModel;
import it.bgm.investments.api.model.LoginResponseModel;
import jakarta.security.auth.message.AuthException;

/**
 * Servizio responsabile dell'autenticazione e della gestione dell'utente corrente.
 * Gestisce il login, il recupero dei dati dell’utente loggato e il logout esplicito.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>Nessun campo dichiarato: l'interfaccia definisce il contratto di autenticazione.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #login(it.bgm.investments.api.model.LoginBodyModel)} —
 *         autentica l’utente con le credenziali fornite e restituisce i dati
 *         e il token di sessione; può lanciare {@link jakarta.security.auth.message.AuthException}
 *         in caso di credenziali non valide.</li>
 *     <li>{@link #currentUser(String)} — restituisce le informazioni
 *         sull’utente associato al {@code jSessionId} corrente.</li>
 *     <li>{@link #logout(String)} — invalida la sessione associata al
 *         {@code jSessionId}, effettuando il logout.</li>
 * </ul>
 */
public interface AuthService {
    LoginResponseModel login(LoginBodyModel body) throws AuthException;

    CurrentUserResponseModel currentUser(String jSessionId);

    void logout(String jSessionId);
}
