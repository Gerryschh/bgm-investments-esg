package it.bgm.investments.repo;

import it.bgm.investments.domain.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository Spring Data JPA dedicato alla gestione dell’entità
 * {@link it.bgm.investments.domain.Asset}, con operazioni CRUD standard
 * e una query personalizzata per la ricerca filtrata.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>Nessun campo dichiarato: l'interfaccia è completamente stateless.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #search(Boolean, String)} —
 *         esegue una ricerca filtrata sugli asset in base allo stato di attivazione
 *         e/o al settore. Entrambi i parametri sono opzionali:
 *         <ul>
 *             <li>{@code activeOnly}: se valorizzato, filtra per asset attivi/inattivi;</li>
 *             <li>{@code settore}: se valorizzato, filtra per settore specifico.</li>
 *         </ul>
 *     </li>
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
public interface AssetRepository extends JpaRepository<Asset, Long> {
    @Query("select a from Asset a where (:activeOnly is null or a.active = :activeOnly) and (:settore is null or a.settore = :settore)")
    List<Asset> search(@Param("activeOnly") Boolean activeOnly, @Param("settore") String settore);
}