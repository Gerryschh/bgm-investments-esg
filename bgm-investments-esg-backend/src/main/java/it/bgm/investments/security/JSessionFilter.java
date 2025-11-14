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