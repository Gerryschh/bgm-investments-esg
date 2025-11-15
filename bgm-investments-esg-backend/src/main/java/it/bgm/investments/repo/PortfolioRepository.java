package it.bgm.investments.repo;

import it.bgm.investments.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA per la gestione dell’entità
 * {@link it.bgm.investments.domain.Portfolio}. Fornisce sia le operazioni
 * CRUD standard sia metodi di ricerca specifici per filtrare i portafogli
 * in base al proprietario.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>Nessun campo dichiarato: l'interfaccia è totalmente stateless.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <!-- Metodi custom dichiarati -->
 *     <li>{@link #findByOwnerId(Long)} — restituisce tutti i portafogli appartenenti a un determinato utente.</li>
 *
 *     <li>{@link #findOwned(Long, Long)} — restituisce un portafoglio solo se appartiene realmente
 *         all’utente indicato, tramite query personalizzata.</li>
 *
 *     <li>Tutti i metodi ereditati da
 *         {@link org.springframework.data.jpa.repository.JpaRepository}, tra cui:
 *         <ul>
 *             <li>{@code save(...)} — salva o aggiorna una simulazione.</li>
 *             <li>{@code findById(...)} — recupera una simulazione tramite ID.</li>
 *             <li>{@code findAll()} — recupera tutte le simulazioni presenti.</li>
 *             <li>{@code deleteById(...)} — elimina una simulazione tramite ID.</li>
 *             <li>{@code count()} — restituisce il numero totale di simulazioni.</li>
 *         </ul>
 *     </li>
 * </ul>
 */
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByOwnerId(Long ownerId);

    @Query("select p from Portfolio p where p.id=:id and p.owner.id=:ownerId")
    Optional<Portfolio> findOwned(@Param("id") Long id, @Param("ownerId") Long ownerId);
}