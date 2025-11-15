package it.bgm.investments.security;

/**
 * Interfaccia astratta per la gestione delle sessioni applicative.
 * Fornisce le operazioni necessarie per creare, validare e invalidare
 * i token di sessione utilizzati dal sistema di autenticazione.
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #create(Long)} — crea una nuova sessione per l’utente
 *         specificato e restituisce il token da utilizzare come JSESSIONID.</li>
 *
 *     <li>{@link #userId(String)} — verifica la validità del token e
 *         restituisce l’ID utente associato, oppure lancia una
 *         {@link SecurityException} se il token non è valido o scaduto.</li>
 *
 *     <li>{@link #invalidate(String)} — invalida il token specificato,
 *         rimuovendo la sessione (logout esplicito).</li>
 * </ul>
 */
public interface SessionStore {
    String create(Long userId);

    Long userId(String token);

    void invalidate(String token);
}