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