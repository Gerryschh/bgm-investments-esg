package it.bgm.investments.security;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Implementazione in-memory di {@link SessionStore} che gestisce token di sessione
 * associati a un utente, con scadenza temporale e pulizia periodica delle sessioni
 * scadute tramite uno scheduler dedicato.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>{@link #B64} — encoder Base64 URL-safe utilizzato per serializzare i token.</li>
 *     <li>{@link #random} — generatore crittograficamente sicuro per creare i token di sessione.</li>
 *     <li>{@link #sessions} — mappa concorrente che associa il token alla relativa {@link Entry}.</li>
 *     <li>{@link #ttlSeconds} — durata (in secondi) della validità di una sessione.</li>
 *     <li>{@link #touchOnAccess} — indica se la scadenza deve essere estesa ad ogni accesso valido.</li>
 *     <li>{@link #reaper} — scheduler che esegue periodicamente la pulizia delle sessioni scadute.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #init()} — inizializza lo {@link #reaper} e pianifica il task di pulizia periodica.</li>
 *     <li>{@link #shutdown()} — arresta lo scheduler di pulizia all'arresto del componente.</li>
 *     <li>{@link #create(Long)} — crea un nuovo token di sessione per l'utente indicato e lo registra in {@link #sessions}.</li>
 *     <li>{@link #userId(String)} — restituisce l'ID utente associato al token; verifica la scadenza e,
 *         se configurato, estende il TTL ad ogni accesso.</li>
 *     <li>{@link #invalidate(String)} — invalida esplicitamente una sessione rimuovendo il token dalla mappa.</li>
 *     <li>{@link #generateToken()} — genera un nuovo token casuale codificato in Base64 URL-safe.</li>
 *     <li>{@link #evictExpired()} — rimuove tutte le sessioni per cui la scadenza è già stata superata.</li>
 * </ul>
 *
 * <p><b>Classe interna:</b></p>
 * <ul>
 *     <li>{@link InMemorySessionStore.Entry} — rappresenta i dati di una singola sessione
 *         (utente, istante di emissione e scadenza).</li>
 * </ul>
 */
@Component
public class InMemorySessionStore implements SessionStore {

    private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();
    private final SecureRandom random = new SecureRandom();
    private final Map<String, Entry> sessions = new ConcurrentHashMap<>();

    @Value("${security.session.ttl-seconds:3600}")      // 1 ora di default
    private long ttlSeconds;

    @Value("${security.session.touch-on-access:true}") // estende TTL ad ogni accesso
    private boolean touchOnAccess;

    private ScheduledExecutorService reaper;

    @PostConstruct
    void init() {
        reaper = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "session-reaper");
            t.setDaemon(true);
            return t;
        });
        reaper.scheduleAtFixedRate(this::evictExpired, ttlSeconds, Math.max(30, ttlSeconds / 6), TimeUnit.SECONDS);
    }

    @PreDestroy
    void shutdown() {
        if (reaper != null) reaper.shutdownNow();
    }

    @Override
    public String create(Long userId) {
        String token = generateToken();
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlSeconds);
        sessions.put(token, new Entry(userId, now, exp));
        return token;
    }

    @Override
    public Long userId(String token) {
        if (token == null || token.isBlank()) {
            throw new SecurityException("JSESSIONID mancante");
        }
        Entry e = sessions.get(token);
        if (e == null) {
            throw new SecurityException("Sessione non valida");
        }
        if (Instant.now().isAfter(e.expiresAt)) {
            sessions.remove(token);
            throw new SecurityException("Sessione scaduta");
        }
        if (touchOnAccess) {
            e.expiresAt = Instant.now().plusSeconds(ttlSeconds);
        }
        return e.userId;
    }

    @Override
    public void invalidate(String token) {
        if (token != null) sessions.remove(token);
    }

    // --- private helpers ---

    private String generateToken() {
        byte[] buf = new byte[32]; // 256-bit
        random.nextBytes(buf);
        return B64.encodeToString(buf);
    }

    private void evictExpired() {
        Instant now = Instant.now();
        sessions.entrySet().removeIf(en -> now.isAfter(en.getValue().expiresAt));
    }

    private static final class Entry {
        private final Long userId;
        private final Instant issuedAt;
        private volatile Instant expiresAt;
        private Entry(Long userId, Instant issuedAt, Instant expiresAt) {
            this.userId = Objects.requireNonNull(userId);
            this.issuedAt = issuedAt;
            this.expiresAt = expiresAt;
        }
    }
}