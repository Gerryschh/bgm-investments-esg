package it.bgm.investments.repo;

import it.bgm.investments.domain.PortfolioPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA dedicato alla gestione delle entità
 * {@link it.bgm.investments.domain.PortfolioPosition}. Fornisce
 * operazioni CRUD standard e metodi di query specifici per filtrare
 * le posizioni in base al portafoglio di appartenenza.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>Nessun campo dichiarato: l'interfaccia è completamente stateless.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <!-- Metodi custom dichiarati nell'interfaccia -->
 *     <li>{@link #findByPortfolioId(Long)} — restituisce tutte le posizioni appartenenti a uno specifico portafoglio.</li>
 *     <li>{@link #findByIdAndPortfolioId(Long, Long)} — restituisce una posizione
 *         in base al suo ID e all'ID del portafoglio, assicurando l’appartenenza.</li>
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
public interface PortfolioPositionRepository extends JpaRepository<PortfolioPosition, Long> {
    List<PortfolioPosition> findByPortfolioId(Long portfolioId);

    Optional<PortfolioPosition> findByIdAndPortfolioId(Long id, Long portfolioId);
}