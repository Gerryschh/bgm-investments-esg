package it.bgm.investments.security;

import it.bgm.investments.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 * Configurazione centrale della sicurezza Spring Security per l’applicazione.
 * Definisce la catena dei filtri, le regole di autorizzazione, il CORS, la gestione
 * stateless delle sessioni e i bean necessari all’autenticazione.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>{@link #allowedOrigin} — origine consentita per le richieste CORS,
 *         iniettata tramite configurazione esterna.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #filter(HttpSecurity, SessionStore, UserRepository)} —
 *         definisce il {@link org.springframework.security.web.SecurityFilterChain} dell’applicazione,
 *         configurando:
 *         <ul>
 *             <li>disabilitazione CSRF</li>
 *             <li>headers (frameOptions)</li>
 *             <li>sessioni stateless</li>
 *             <li>CORS con impostazioni dinamiche</li>
 *             <li>regole di autorizzazione per endpoint pubblici, autenticati e amministrativi</li>
 *             <li>inserimento del filtro custom {@link JSessionFilter}</li>
 *         </ul>
 *     </li>
 *
 *     <li>{@link #passwordEncoder()} — definisce il bean {@link PasswordEncoder}
 *         basato su {@link org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder}
 *         utilizzato per l’hashing delle password.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${bgm.cors.allowed-origin}")
    String allowedOrigin;

    @Bean
    SecurityFilterChain filter(HttpSecurity http,
                               SessionStore sessions,
                               UserRepository userRepository) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(r -> {
                    CorsConfiguration c = new CorsConfiguration();
                    c.setAllowedOrigins(List.of(allowedOrigin));
                    c.setAllowedHeaders(List.of("Content-Type", "Authorization", "JSESSIONID"));
                    c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                    c.setAllowCredentials(true);
                    return c;
                }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/bgm-investments-esg-backend/v1/auth/login", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/h2-console/**").permitAll()
                        .requestMatchers("/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JSessionFilter(sessions, userRepository), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}