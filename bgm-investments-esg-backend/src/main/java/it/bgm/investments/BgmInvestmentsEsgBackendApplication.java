package it.bgm.investments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale dell’applicazione Spring Boot. Avvia il contesto
 * applicativo e inizializza tutti i componenti dichiarati nel progetto.
 *
 * <p><b>Campi:</b></p>
 * <ul>
 *     <li>Nessun campo dichiarato: la classe funge esclusivamente da entry point.</li>
 * </ul>
 *
 * <p><b>Metodi:</b></p>
 * <ul>
 *     <li>{@link #main(String[])} —
 *         metodo di ingresso dell’applicazione che avvia il contesto Spring
 *         tramite {@link org.springframework.boot.SpringApplication#run(Class, String...)}.</li>
 * </ul>
 */
@SpringBootApplication()
public class BgmInvestmentsEsgBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BgmInvestmentsEsgBackendApplication.class, args);
    }
}
