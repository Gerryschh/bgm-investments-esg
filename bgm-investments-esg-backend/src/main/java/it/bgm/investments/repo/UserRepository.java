package it.bgm.investments.repo;

import it.bgm.investments.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository Spring Data JPA dedicato alla gestione dell’entità
 * {@link it.bgm.investments.domain.User}. Fornisce operazioni CRUD standard
 * e un metodo specifico per la ricerca dell’utente tramite email.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>Nessun campo dichiarato: l’interfaccia è completamente stateless.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <!-- Metodo custom -->
 *     <li>{@link #findByEmail(String)} — restituisce un utente sulla base dell’indirizzo email.</li>
 *
 *     <!-- Metodi ereditati da JpaRepository -->
 *     <li>Tutti i metodi ereditati da
 *         {@link org.springframework.data.jpa.repository.JpaRepository}, tra cui:
 *         <ul>
 *             <li>{@code save(...)} — salva o aggiorna un utente.</li>
 *             <li>{@code findById(...)} — recupera un utente tramite ID.</li>
 *             <li>{@code findAll()} — restituisce l’elenco completo degli utenti.</li>
 *             <li>{@code deleteById(...)} — elimina un utente tramite ID.</li>
 *             <li>{@code count()} — restituisce il numero totale di utenti.</li>
 *         </ul>
 *     </li>
 * </ul>
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}