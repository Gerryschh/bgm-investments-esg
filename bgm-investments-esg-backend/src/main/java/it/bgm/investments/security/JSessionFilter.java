package it.bgm.investments.security;

import it.bgm.investments.domain.User;
import it.bgm.investments.repo.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro di sicurezza che legge un token di sessione (header {@code JSESSIONID}/{@code Jsessionid}),
 * lo valida tramite {@link SessionStore} e, se valido, popola il contesto di sicurezza di Spring
 * con un {@link UserPrincipal} costruito a partire dall'utente memorizzato nel database.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>{@link #sessions} — store delle sessioni utilizzato per validare il token e ricavare l'ID utente.</li>
 *     <li>{@link #userRepository} — repository impiegato per caricare l'entità {@link it.bgm.investments.domain.User}
 *         e determinare i ruoli associati.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #JSessionFilter(SessionStore, UserRepository)} — costruttore che inizializza il filtro
 *         con lo store di sessione e il repository utenti da utilizzare durante la validazione.</li>
 *
 *     <li>{@link #doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)} — metodo chiamato
 *         per ogni richiesta; estrae il token di sessione dagli header, verifica l'autenticazione corrente,
 *         tenta di risolvere l'utente associato e, in caso di successo, imposta l'autenticazione nel
 *         {@link org.springframework.security.core.context.SecurityContextHolder} prima di proseguire
 *         con la catena di filtri.</li>
 * </ul>
 */
public class JSessionFilter extends OncePerRequestFilter {

    private final SessionStore sessions;
    private final UserRepository userRepository;

    public JSessionFilter(SessionStore sessions, UserRepository userRepository) {
        this.sessions = sessions;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("JSESSIONID");
        if (!StringUtils.hasText(token)) {
            token = request.getHeader("Jsessionid");
        }

        if (StringUtils.hasText(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Long userId = sessions.userId(token); // valida + scadenza
                // Carica utente per assegnare ruoli (USER/ADMIN)
                User user = userRepository.findById(userId)
                        .orElse(null);
                if (user != null) {
                    List<SimpleGrantedAuthority> auths =
                            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRuolo())); // es. ROLE_ADMIN
                    UserPrincipal principal = new UserPrincipal(user.getId(), user.getEmail(), auths);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(principal, null, auths);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (SecurityException ex) {
                // Token non valido/scaduto: NON settiamo il contesto; lasceremo che la security blocchi se la rotta è protetta
            }
        }

        filterChain.doFilter(request, response);
    }
}