package it.bgm.investments.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Implementazione di {@link org.springframework.security.core.userdetails.UserDetails}
 * che rappresenta l’utente autenticato nel sistema. Incapsula l’ID utente,
 * lo username e le autorità assegnate, fornendo le informazioni richieste da
 * Spring Security per popolare il contesto di autenticazione.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>{@link #userId} — identificativo dell’utente autenticato.</li>
 *     <li>{@link #username} — valore usato come nome dell’utente nel contesto Security.</li>
 *     <li>{@link #authorities} — insiemi dei ruoli/permessi dell’utente (es. <code>ROLE_ADMIN</code>).</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #UserPrincipal(Long, String, Collection)} — costruttore che inizializza l’utente autenticato.</li>
 *     <li>{@link #getUserId()} — restituisce l’ID utente.</li>
 *
 *     <!-- Metodi di UserDetails -->
 *     <li>{@link #getAuthorities()} — restituisce i ruoli/permessi dell’utente.</li>
 *     <li>{@link #getPassword()} — sempre <code>null</code>, perché la password non è conservata in sessione.</li>
 *     <li>{@link #getUsername()} — restituisce lo username dell’utente.</li>
 *     <li>{@link #isAccountNonExpired()} — indica che l’account non è scaduto.</li>
 *     <li>{@link #isAccountNonLocked()} — indica che l’account non è bloccato.</li>
 *     <li>{@link #isCredentialsNonExpired()} — indica che le credenziali sono valide.</li>
 *     <li>{@link #isEnabled()} — indica che l’utente è attivo.</li>
 * </ul>
 */
public class UserPrincipal implements UserDetails {
    private final Long userId;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long userId, String username, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}