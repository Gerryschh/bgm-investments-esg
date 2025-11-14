package it.bgm.investments.security;

public interface SessionStore {
    /**
     * Crea una nuova sessione per l'utente indicato e restituisce il token (da mettere in JSESSIONID).
     */
    String create(Long userId);

    /**
     * Restituisce l'ID utente associato al token, se valido; altrimenti lancia eccezione.
     */
    Long userId(String token);

    /**
     * Invalida il token (logout esplicito).
     */
    void invalidate(String token);
}