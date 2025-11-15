package it.bgm.investments.web;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Classe base di supporto per i vari *ApiDelegate*, fornendo utilità comuni
 * per l’estrazione del token di sessione dalle richieste HTTP. Consente ai
 * delegate REST di ottenere in modo uniforme il valore del cookie/header
 * {@code JSESSIONID}, indipendentemente dalla variante usata dal client.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>Nessun campo dichiarato: la classe fornisce solo funzionalità utility.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #getJSessionId(jakarta.servlet.http.HttpServletRequest)} —
 *         estrae il valore del token di sessione cercandolo negli header
 *         (in più varianti di capitalizzazione) e, se non presente, nei cookie
 *         della richiesta HTTP.</li>
 * </ul>
 */
public abstract class BaseApiDelegate {
    String getJSessionId(HttpServletRequest request) {
        String id = request.getHeader("JSESSIONID");
        if (id == null) id = request.getHeader("jsessionid");
        if (id == null) id = request.getHeader("JSessionid");
        if (id == null && request.getCookies() != null) {
            for (var c : request.getCookies()) {
                if ("JSESSIONID".equalsIgnoreCase(c.getName())) {
                    id = c.getValue();
                    break;
                }
            }
        }
        return id;
    }
}