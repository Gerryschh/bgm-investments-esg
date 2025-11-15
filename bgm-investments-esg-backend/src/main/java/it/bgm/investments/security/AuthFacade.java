package it.bgm.investments.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Componente di supporto che incapsula l'accesso al sistema di sessioni
 * tramite {@link SessionStore}, fornendo un punto centralizzato per
 * ottenere l’identificativo utente associato a una sessione.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>{@link #sessions} — store delle sessioni utilizzato per risolvere l'utente autenticato.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #userId(String)} — restituisce l’ID dell’utente associato
 *         al valore {@code jsessionId} ricevuto.</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class AuthFacade {

    private final SessionStore sessions;

    public Long userId(String jsessionId) {
        return sessions.userId(jsessionId);
    }
}
