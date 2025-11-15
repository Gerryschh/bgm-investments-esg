package it.bgm.investments.repo;

import it.bgm.investments.domain.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository Spring Data JPA dedicato alla gestione dell’entità
 * {@link it.bgm.investments.domain.Simulation}. Fornisce tutte le operazioni
 * CRUD e i metodi standard messi a disposizione dall'interfaccia
 * {@link org.springframework.data.jpa.repository.JpaRepository}.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>Nessun campo dichiarato: l'interfaccia è interamente stateless.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
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
public interface SimulationRepository extends JpaRepository<Simulation, Long> {
}