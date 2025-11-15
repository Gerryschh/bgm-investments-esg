package it.bgm.investments.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configurazione centrale di OpenAPI/Swagger per l'applicazione.
 * <p>
 * Questo bean personalizza le informazioni visualizzate in Swagger UI
 * (titolo, versione, descrizione, server, sicurezza, tag) a partire
 * dalle esigenze del dominio BGM Investments ESG.
 * <p>
 * La documentazione è resa disponibile tramite gli endpoint esposti da
 * Springdoc (es. <code>/v3/api-docs</code> e <code>/swagger-ui/index.html</code>).
 */
@Configuration
public class OpenApiConfig {

    /**
     * Definisce il modello OpenAPI mostrato in Swagger UI.
     * <p>
     * Qui vengono impostati:
     * <ul>
     *     <li>metadati generali dell'API (titolo, versione, descrizione);</li>
     *     <li>server di base per le chiamate;</li>
     *     <li>schema di sicurezza basato su header <code>JSESSIONID</code>;</li>
     *     <li>tag principali per raggruppare le operazioni.</li>
     * </ul>
     *
     * @return istanza configurata di {@link OpenAPI}.
     */
    @Bean
    public OpenAPI bgmInvestmentsOpenAPI() {
        // Info principale (presa dal tuo openapi.yml)
        Info info = new Info()
                .title("BGM Investments ESG - API")
                .version("1.0.0")
                .description(
                        "API per: autenticazione utenti (login, utente in sessione, logout); " +
                                "consultazione e gestione degli asset (ricerca, dettaglio, inserimento, modifica, disattivazione); " +
                                "gestione dei portafogli personali (creazione, elenco); gestione delle posizioni dei portafogli " +
                                "(lettura, inserimento, rimozione); simulazioni di scenario d’investimento su un portafoglio. " +
                                "L’accesso alle rotte protette avviene inviando nell’header JSESSIONID il token di sessione " +
                                "ricevuto dalla login."
                );

        // Server base (equivalente a host + basePath del tuo swagger: localhost:8080 + /bgm-investments-esg-backend/v1)
        Server server = new Server()
                .url("localhost:8080/bgm-investments-esg-backend/v1")
                .description("API base path v1");

        // Security per header JSESSIONID
        SecurityScheme jsessionScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("JSESSIONID")
                .description("Token di sessione restituito dall'endpoint di login.");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("JSESSIONID");

        Components components = new Components()
                .addSecuritySchemes("JSESSIONID", jsessionScheme);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server))
                .components(components)
                .security(List.of(securityRequirement));
    }
}